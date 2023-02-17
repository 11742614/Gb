package com.trs.gb.bean;

import cn.afterturn.easypoi.excel.annotation.Excel;

import java.util.Date;

/**
 * @Auther: guodongfeng
 * @Date: 2021/8/20 12:08
 * @Description:
 */
public class SearchLog {
    @Excel(name = "序号", width = 20, orderNum = "1")
    private Integer id;
    private String ip;
    @Excel(name = "检索词", width = 20, orderNum = "2")
    private String searchWord;
    private String searchTime;
    @Excel(name = "检索次数", width = 20, orderNum = "3")
    private Long count;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSearchWord() {
        return searchWord;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    public String getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(String searchTime) {
        this.searchTime = searchTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
