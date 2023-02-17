package com.trs.gb.bean;

public class RequestBean {


    private String chnls;
    private  String chnlslimit;
    private String searchScope;
    private String classification;
    private  String timeScope;
    private String sortOrder;
    private String SearchWord;
    private Integer pageNum;
    private Integer pageSize;
    private Integer cutsize;
    private String notchnls;


    public String getChnlslimit() {
        return chnlslimit;
    }

    public void setChnlslimit(String chnlslimit) {
        this.chnlslimit = chnlslimit;
    }
    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getChnls() {
        return chnls;
    }

    public void setChnls(String chnls) {
        this.chnls = chnls;
    }

    public String getSearchScope() {
        return searchScope;
    }

    public void setSearchScope(String searchScope) {
        this.searchScope = searchScope;
    }

    public String getTimeScope() {
        return timeScope;
    }

    public void setTimeScope(String timeScope) {
        this.timeScope = timeScope;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSearchWord() {
        return SearchWord;
    }

    public void setSearchWord(String searchWord) {
        this.SearchWord = searchWord;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }


    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public Integer getCutsize() {
        return cutsize;
    }

    public void setCutsize(Integer cutsize) {
        this.cutsize = cutsize;
    }


    public String getNotchnls() {
        return notchnls;
    }

    public void setNotchnls(String notchnls) {
        this.notchnls = notchnls;
    }

}
