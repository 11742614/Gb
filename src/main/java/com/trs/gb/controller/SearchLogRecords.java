package com.trs.gb.controller;

import com.trs.gb.bean.SearchLog;
import com.trs.gb.tools.CkmUtil;
import com.trs.gb.tools.GbUtil;
import com.trs.gb.tools.IpUtils;
import com.trs.gb.tools.MyExcelExportUtil;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: guodongfeng
 * @Date: 2021/8/20 11:40
 * @Description: 检索日志记录
 */
@RestController
public class SearchLogRecords {
   /* @RequestMapping("/addLog")
    public void addLog(HttpServletRequest request,String searchWord){
        GbUtil gbUtil = new GbUtil();
        gbUtil.insert(request,searchWord);
    }*/
    //导出日志记录
    @RequestMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response){
        GbUtil gbUtil = new GbUtil();
        List<SearchLog> list = gbUtil.searchWordCategory();
        MyExcelExportUtil.exportExcel(list,SearchLog.class,"检索词统计","检索词统计",response);
    }

    //查询相关短语
    @RequestMapping("/searchDy")
    public String searchDy(String word){
        CkmUtil ckmUtil = new CkmUtil();
        return ckmUtil.getDy(word);
    }

    //查询相关短语
    @RequestMapping("/pinyinToChinese")
    public String pinyinToChinese(String word){
        CkmUtil ckmUtil = new CkmUtil();
        String PATTERN="^[a-zA-Z]+$";
        boolean matches = word.matches(PATTERN);
        if(matches){
            word = ckmUtil.pinyinToChinese(word);
        }
        return word;
    }

}
