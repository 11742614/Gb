package com.trs.gb.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.Map;

public class OtherZlBean {
    /*展厅名称*/
   // @JSONField(ordinal=16)
    //String  zt_name="";
    /*展览名称*/
    @JSONField(ordinal=2)
    String  exhi_title="";
    /*展览类型*/
    @JSONField(ordinal=3)
    String  type="";
    /*对外显示展览时间*/
    @JSONField(ordinal=4)
    String  date="";
    /*展览开始时间*/
    @JSONField(ordinal=5)
    String  start_time="";
    /*展览结束时间*/
    @JSONField(ordinal=6)
    String  end_time="";
    /*地点*/
    @JSONField(ordinal=7)
    String  venue="";
    /*主办单位*/
    @JSONField(ordinal=8)
    String  host="";
    /*协办单位*/
    @JSONField(ordinal=9)
    String  organizer="";
    /*赞助单位*/
   // String  co_organizer="";
    /*横向海报图，比例16:9*/
    @JSONField(ordinal=10)
    String  hor_poster="";
    /*竖向海报图，比例3:4*/
    @JSONField(ordinal=11)
    String  ver_poster="";
    /*展览介绍*/
    @JSONField(ordinal=12)
    String  descr="";
    /*文章链接*/
    //String DOCPUBURL="";

    //String cp_zlID="";
    //String zl_ztID="";
    /*展厅ID*/
    @JSONField(ordinal=1)
    String id="";
    @JSONField(ordinal=13)
    List<Highlights> highlights;
    @JSONField(ordinal=14)
    List<Scenephotos> scenephotos;

  /*  public String getZt_name() {
        return zt_name;
    }

    public void setZt_name(String zt_name) {
        this.zt_name = zt_name;
    }*/

    public String getExhi_title() {
        return exhi_title;
    }

    public void setExhi_title(String exhi_title) {
        this.exhi_title = exhi_title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    /*public String getCo_organizer() {
        return co_organizer;
    }

    public void setCo_organizer(String co_organizer) {
        this.co_organizer = co_organizer;
    }
*/
    public String getHor_poster() {
        return hor_poster;
    }

    public void setHor_poster(String hor_poster) {
        this.hor_poster = hor_poster;
    }

    public String getVer_poster() {
        return ver_poster;
    }

    public void setVer_poster(String ver_poster) {
        this.ver_poster = ver_poster;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    /*public String getDOCPUBURL() {
        return DOCPUBURL;
    }

    public void setDOCPUBURL(String DOCPUBURL) {
        this.DOCPUBURL = DOCPUBURL;
    }

    public String getCp_zlID() {
        return cp_zlID;
    }

    public void setCp_zlID(String cp_zlID) {
        this.cp_zlID = cp_zlID;
    }

    public String getZl_ztID() {
        return zl_ztID;
    }

    public void setZl_ztID(String zl_ztID) {
        this.zl_ztID = zl_ztID;
    }
*/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Highlights> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<Highlights> highlights) {
        this.highlights = highlights;
    }

    public List<Scenephotos> getScenephotos() {
        return scenephotos;
    }

    public void setScenephotos(List<Scenephotos> scenephotos) {
        this.scenephotos = scenephotos;
    }
}
