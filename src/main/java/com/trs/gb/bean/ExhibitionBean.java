package com.trs.gb.bean;

import java.util.List;
import java.util.Map;

public class ExhibitionBean {
    /*展厅名称*/
    String  zt_name="";
    /*展览名称*/
    String  zl_name="";
    /*展览类型*/
    String  zllb="";
    /*对外显示展览时间*/
    String  qtxszqsj="";
    /*展览开始时间*/
    String  starttime="";
    /*展览结束时间*/
    String  endtime="";
    /*地点*/
    String  zldd="";
    /*主办单位*/
    String  zbdw="";
    /*协办单位*/
    String  cbdw="";
    /*赞助单位*/
    String  zcdw="";
    /*横向海报图，比例16:9*/
    String  zltt="";
    /*竖向海报图，比例3:4*/
    String  zlhb="";
    /*展览介绍*/
    String  zljs="";
    /*展品名称*/
    String  cp_name="";
    /*展品年代*/
    String  nd="";
    /*展品尺寸*/
    String  gg="";
    /*展品图片*/
    String  slt="";
    /*展品介绍 */
    String  wwms="";
    /*展厅实景照片信息字段*/
    String  zttp="";
    /*照片*/
    String   tp="";
    /*照片描述*/
    String   mc="";
    /*文章链接*/
    String DOCPUBURL="";

    String cp_zlID="";
    String zl_ztID="";

    String id="";
    List<Map<String,String>> highlights;
    List<Map<String,String>> scenephotos;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZt_name() {
        return zt_name;
    }

    public void setZt_name(String zt_name) {
        this.zt_name = zt_name;
    }

    public String getZl_name() {
        return zl_name;
    }

    public void setZl_name(String zl_name) {
        this.zl_name = zl_name;
    }

    public String getZllb() {
        return zllb;
    }

    public void setZllb(String zllb) {
        this.zllb = zllb;
    }

    public String getQtxszqsj() {
        return qtxszqsj;
    }

    public void setQtxszqsj(String qtxszq) {
        this.qtxszqsj = qtxszq;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getZldd() {
        return zldd;
    }

    public void setZldd(String zldd) {
        this.zldd = zldd;
    }

    public String getZbdw() {
        return zbdw;
    }

    public void setZbdw(String zbdw) {
        this.zbdw = zbdw;
    }

    public String getCbdw() {
        return cbdw;
    }

    public void setCbdw(String cbdw) {
        this.cbdw = cbdw;
    }

    public String getZcdw() {
        return zcdw;
    }

    public void setZcdw(String zcdw) {
        this.zcdw = zcdw;
    }

    public String getZltt() {
        return zltt;
    }

    public void setZltt(String zltt) {
        this.zltt = zltt;
    }

    public String getZlhb() {
        return zlhb;
    }

    public void setZlhb(String zlhb) {
        this.zlhb = zlhb;
    }

    public String getZljs() {
        return zljs;
    }

    public void setZljs(String zljs) {
        this.zljs = zljs;
    }

    public String getCp_name() {
        return cp_name;
    }

    public void setCp_name(String cp_name) {
        this.cp_name = cp_name;
    }

    public String getDOCPUBURL() {
        return DOCPUBURL;
    }

    public void setDOCPUBURL(String DOCPUBURL) {
        this.DOCPUBURL = DOCPUBURL;
    }

    public String getNd() {
        return nd;
    }

    public void setNd(String nd) {
        this.nd = nd;
    }

    public String getGg() {
        return gg;
    }

    public void setGg(String gg) {
        this.gg = gg;
    }

    public String getSlt() {
        return slt;
    }

    public void setSlt(String slt) {
        this.slt = slt;
    }

    public String getWwms() {
        return wwms;
    }

    public void setWwms(String wwms) {
        this.wwms = wwms;
    }

    public String getZttp() {
        return zttp;
    }

    public void setZttp(String zttp) {
        this.zttp = zttp;
    }

    public String getTp() {
        return tp;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
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


    public List<Map<String, String>> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<Map<String, String>> highlights) {
        this.highlights = highlights;
    }

    public List<Map<String, String>> getScenephotos() {
        return scenephotos;
    }

    public void setScenephotos(List<Map<String, String>> scenephotos) {
        this.scenephotos = scenephotos;
    }

}
