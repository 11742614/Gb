package com.trs.gb.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trs.gb.tools.IpUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by WFX1024 on 2019/1/16.
 */
@Aspect
@Component
public class WeblogAspect {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(WeblogAspect.class);

    //@Pointcut("execution(public * com.trs.gb.controller.*.*(..))")//两个..代表所有子目录，最后括号里的两个..代表所有参数
    @Pointcut("execution(public * com.trs.gb.controller.TestBootController.*(..))") //只拦截检索，不拦截热词
    public void logPointCut() {
    }

    @Before("logPointCut()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Object obj[] = joinPoint.getArgs().clone();
        List<Object> params = Stream.of(obj).filter(arg -> (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)))
                .collect(Collectors.toList());
        JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(params));
        if (jsonArray.size()>0) {
            JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(0).toString());
            if (jsonObject.get("searchWord") == null) {
                String searchword = "";
                jdbcTemplate.update("INSERT INTO logforgbdev(`ip`,`address`,`time`,method,param,searchword) VALUES (?,?,?,?,?,?)",
                        new Object[]{IpUtils.getIpAddr(request), request.getRequestURL().toString(),
                                format.format(new Date()), request.getMethod(), JSON.toJSONString(params), searchword});
            } else {
                logger.info("检索关键词：" + jsonObject.get("searchWord").toString());
                jdbcTemplate.update("INSERT INTO logforgbdev(`ip`,`address`,`time`,method,param,searchword) VALUES (?,?,?,?,?,?)",
                        new Object[]{IpUtils.getIpAddr(request), request.getRequestURL().toString(),
                                format.format(new Date()), request.getMethod(), JSON.toJSONString(params), jsonObject.get("searchWord")});
            }
        }else {
            String searchword = "";
            jdbcTemplate.update("INSERT INTO logforgbdev(`ip`,`address`,`time`,method,param,searchword) VALUES (?,?,?,?,?,?)",
                    new Object[]{IpUtils.getIpAddr(request), request.getRequestURL().toString(),
                            format.format(new Date()), request.getMethod(), JSON.toJSONString(params), searchword});
        }

        // 记录下请求内容
        logger.info("请求地址 : " + request.getRequestURL().toString());
        logger.info("HTTP METHOD : " + request.getMethod());
        // 获取真实的ip地址
        logger.info("IP : " + IpUtils.getIpAddr(request));
        logger.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "."
                + joinPoint.getSignature().getName());
        logger.info("参数 : " + JSON.toJSONString(params));
    }

    @AfterReturning(returning = "ret", pointcut = "logPointCut()")// returning的值和doAfterReturning的参数名一致
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容(返回值太复杂时，打印的是物理存储空间的地址)
        logger.info("返回值 : " + ret);
    }

    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.currentTimeMillis();
            Object ob = pjp.proceed();// ob 为方法的返回值
        logger.info("耗时 : " + (System.currentTimeMillis() - startTime));
        return ob;
    }
}