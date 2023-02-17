package com.trs.gb.tools;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.trs.commons.lang.utils.StringUtils;
import com.trs.gb.bean.WinXinEntity;
import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class WeinXinUtil {

    public static WinXinEntity getWinXinEntity(String url,RedisConfig redisConfig) {
        WinXinEntity wx = new WinXinEntity();
        GbUtil gbUtil = new GbUtil();
        String access_token = "";
        JedisPool jedisPool = redisConfig.getJedisPool();
        Jedis resource = jedisPool.getResource();
        try {

            String token = "";
            /**
             * 把token存到redis缓存中
             * */
            if (StringUtils.isBlank(resource.get("token"))) {
                resource.set("token", getAccessToken());
                resource.expire("token", 7200);
            }
            access_token = resource.get("token");
            /**
             * 把ticket存到redis缓存中
             */
            String ticket = "";
            if (StringUtils.isBlank(resource.get("ticket"))) {
                resource.set("ticket", getTicket(access_token));
                resource.expire("ticket", 7200);
            }
            ticket = resource.get("ticket");
            Map<String, String> ret = Sign.sign(ticket, url);
            wx.setTicket(ret.get("jsapi_ticket"));
            wx.setSignature(ret.get("signature"));
            wx.setNoncestr(ret.get("nonceStr"));
            wx.setTimestamp(ret.get("timestamp"));
            wx.setAppId(gbUtil.getVlaue("AppId"));
            System.out.println("\n\n" + ret.toString() + "\n\n");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            resource.close();
        }
        return wx;
    }

    // 获取token
    private static String getAccessToken() {
        GbUtil gbUtil = new GbUtil();
        String access_token = "";
        String grant_type = "client_credential";// 获取access_token填写client_credential
        String AppId = gbUtil.getVlaue("AppId");// 第三方用户唯一凭证
        String secret = gbUtil.getVlaue("secret");// 第三方用户唯一凭证密钥，即appsecret
        // 这个url链接地址和参数皆不能变
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=" + grant_type + "&appid=" + AppId + "&secret="
                + secret; // 访问链接

        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            /*
             * System.setProperty("sun.net.client.defaultConnectTimeout",
             * "30000");// 连接超时30秒
             * System.setProperty("sun.net.client.defaultReadTimeout", "30000");
             * // 读取超时30秒
             */
            http.connect();
            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            String message = new String(jsonBytes, "UTF-8");
            JSONObject demoJson = JSONObject.fromObject(message);
            access_token = demoJson.getString("access_token");
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return access_token;
    }

    // 获取ticket
    private static String getTicket(String access_token) {
        String ticket = null;
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + access_token + "&type=jsapi";// 这个url链接和参数不能变
        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
            http.connect();
            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            String message = new String(jsonBytes, "UTF-8");
            JSONObject demoJson = JSONObject.fromObject(message);
            ticket = demoJson.getString("ticket");
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ticket;
    }
}