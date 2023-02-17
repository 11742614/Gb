package com.trs.gb.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by WFX1024 on 2019/1/17.
 */
@RestController
@RequestMapping("/hotword")
public class ForWcmHotWord {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(value = "/get",produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getHotword(){
        List<Map<String, Object>> maps =
                jdbcTemplate.queryForList("SELECT count(*) as times,DATE_FORMAT(max(time),'%Y-%m-%d %H:%i:%s') as lasttime,searchword from logforgbdev WHERE trim(searchword) !='' GROUP BY searchword");
        for (int i = 0;i< maps.size();i++){
            maps.get(i).put("times",maps.get(i).get("times").toString());
        }
        return JSON.toJSONString(maps);
    }
}
