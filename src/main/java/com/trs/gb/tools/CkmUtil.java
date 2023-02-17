package com.trs.gb.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.trs.commons.lang.utils.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @Auther: guodongfeng
 * @Date: 2021/8/27 15:53
 * @Description:
 */
public class CkmUtil {
    //获取配置文件
    public String getVlaue(String Key) {
        String vlaue = "";
        try {
            Properties prop = new Properties();
            InputStream is = this.getClass().getResourceAsStream("/config.properties");
            prop.load(is);
            vlaue = prop.getProperty(Key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vlaue;
    }
    //拼音转汉字
    public String pinyinToChinese(String pinyin) {
        String url = getVlaue("ckmUrl");
        System.out.println("ckm连接地址："+url);
        String keywordUrl = url + "/rs/py/search";
        String keyword = "";
        //调用接口
        Map<String, String> map = new HashMap<>();
        map.put("pinyin", pinyin);
        map.put("model", "demo");
        //map.put("numOfSub","1");
        JSONObject json = HttpUtil.postForForm(keywordUrl, map);
        //解析数据
        int code = json.getInteger("code");
        System.out.println("拼音查询数据返回结果："+json.toJSONString());
        if (code == 1) {
            JSONArray pinyinItems = (JSONArray) json.get("pinyinItems");
            if(pinyinItems.size()>0){
                JSONObject pinyinWord = (JSONObject) pinyinItems.get(0);
                keyword = pinyinWord.get("py").toString();
            }
        }else{
            keyword=pinyin;
        }
        return keyword;
    }

    //获取相关短语
    public String getDy(String searchWord) {
        String result="";
        String url = getVlaue("ckmUrl");
        System.out.println("ckm连接地址："+url);
        String keywordUrl = url + "/rs/dy/search";
        //调用接口
        Map<String, String> map = new HashMap<>();
        map.put("query", searchWord);
        map.put("option", "2");
        map.put("model", "demo");
        JSONObject json = HttpUtil.postForForm(keywordUrl, map);
        System.out.println("短语查询数据返回结果："+json.toJSONString());
        //解析数据
        int code = json.getInteger("code");
        if (code == 1) {
            JSONArray dyResult = (JSONArray) json.get("dyResult");
           /* for (int i=0;i<dyResult.size();i++){
                String str = dyResult.get(i).toString();
                if(!str.contains(searchWord)){
                    dyResult.remove(i);
                }
            }*/
            //list = dyResult.parseArray(dyResult.toJSONString(),String.class);
            result = JSON.toJSONString(dyResult, SerializerFeature.DisableCircularReferenceDetect);
        }
        return result;
    }
    public static String concertString(String keyWord) {
        String [] chararrays={"\\(","\\)","\\[","]","，","/","@","=",">","<","!","&","\\*","\\^","-","\\+","%","\\?","'"};
        for (int i=0;i<chararrays.length;i++){
            keyWord=keyWord.replaceAll(chararrays[i],"\\\\"+chararrays[i]);
        }
        return keyWord;
    }
}
