package com.trs.gb.tools;


import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 苏亚青 on 2019/1/13.
 */
@Aspect
@Component
public class RequestLimitContract{
    @Autowired
    private JedisPool jedisPool;
    //private static final Logger logger = LoggerFactory.getLogger("RequestLimitLogger");
    @Before("execution(* com.trs.gb.controller.*.*(..)) && @annotation(limit)")
    public void requestLimit(final JoinPoint joinPoint, RequestLimit limit) throws RequestLimitException  {
        try {
            long count = limit.count();
            int time = limit.time();
            Object[] args = joinPoint.getArgs();
            HttpServletRequest request = null;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof HttpServletRequest) {
                    request = (HttpServletRequest) args[i];
                    break;
                }
            }
            if (request == null) {
                throw new RequestLimitException("方法中缺失HttpServletRequest参数");
            }

            String ip = IpUtils.getIpAddr(request);
            String url = request.getRequestURL().toString();
            String key = "req_limit_".concat(url).concat(ip);
            /** 操作redis cache **/
            String cacheKey = "visit_limit_"+url+"_"+ip;
            // Cache cache = Redis.use(PropKit.get("redis.cachename"));
            Jedis jedis= jedisPool.getResource();
            Long visitNum=jedis.incr(cacheKey);
            // 如果redis中的count大于限制的次数，则报错
            Map<String,String> map =new HashMap<>();
            if (visitNum > count) {
                if (jedis.ttl(cacheKey)>600) {
                    jedis.expire(cacheKey, time);
                }
                // logger.info("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数[" + limit.count() + "]");
                map.put("user","用户IP:"+ip );
                map.put("msg","超过了限定的次数"+limit.count());
                throw new RequestLimitException (JSON.toJSONString(map));
            }else {
                jedis.expire(cacheKey, 60*60);
            }

        } catch (RequestLimitException  e) {
            throw e;
        }
    }
}
