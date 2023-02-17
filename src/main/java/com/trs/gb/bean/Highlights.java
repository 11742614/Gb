package com.trs.gb.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by 苏亚青 on 2019/1/13.
 */
public class Highlights{

    /*展品名称*/
    @JSONField(ordinal = 1)
    String highlights_name = "";
    /*展品年代*/
    //String  age="";
    /*展品尺寸*/
    // String  size="";
    /*展品图片*/
    @JSONField(ordinal = 2)
    String high_image = "";
    /*展品介绍 */
    @JSONField(ordinal = 3)
    String highlights_intro = "";

    public String getHighlights_name() {
        return highlights_name;
    }

    public void setHighlights_name(String highlights_name) {
        this.highlights_name = highlights_name;
    }

    public String getHigh_image() {
        return high_image;
    }

    public void setHigh_image(String high_image) {
        this.high_image = high_image;
    }

    public String getHighlights_intro() {
        return highlights_intro;
    }

    public void setHighlights_intro(String highlights_intro) {
        this.highlights_intro = highlights_intro;
    }
}