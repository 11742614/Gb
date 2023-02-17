package com.trs.gb.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * @author 陈伟, chenwei@maichengvip.com on 7/20/21
 * @version 1.0
 */
public class HttpUtil{

    public static JSONObject postForForm(String url, Map<String, String> parms) {
        HttpPost httpPost = new HttpPost(url);
        ArrayList<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
        parms.forEach((key, value) -> list.add(new BasicNameValuePair(key, value)));
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            if (Objects.nonNull(parms) && parms.size() >0) {
                httpPost.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
            }
            InputStream content = httpPost.getEntity().getContent();
            InputStreamReader inputStreamReader = new InputStreamReader(content, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String readLine = bufferedReader.readLine();
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            JSONObject jsonObject = JSON.parseObject(EntityUtils.toString(entity, "UTF-8"));
            return jsonObject;
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (Objects.nonNull(httpClient)){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
