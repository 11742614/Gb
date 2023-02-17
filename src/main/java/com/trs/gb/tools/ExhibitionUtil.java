package com.trs.gb.tools;

import com.trs.gb.bean.ExhibitionBean;
import com.trs.gb.bean.Highlights;
import com.trs.gb.bean.OtherZlBean;
import com.trs.gb.bean.Scenephotos;
import com.trs.hybase.client.TRSConnection;
import com.trs.hybase.client.TRSException;
import com.trs.hybase.client.TRSRecord;
import com.trs.hybase.client.TRSResultSet;
import com.trs.hybase.client.params.SearchParams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExhibitionUtil {
    private static SearchParams param=new SearchParams();
    private static TRSResultSet resultSet = null;


    public static Map<String, Object> ShowExhibitionUtil(TRSConnection connection){
        //获取当前日期
        Date nowdate = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");

        GbUtil gbUtil=new GbUtil();
        String Sources=gbUtil.getVlaue("Sources");

        List<ExhibitionBean> exhibitionBeansZL=new ArrayList<>();
        List<ExhibitionBean> exhibitionBeansZT=new ArrayList<>();
        List<ExhibitionBean> exhibitionBeanRequest=new ArrayList<>();
        String sqlzl="endtime:["+dateFormat.format(nowdate)+" TO  *} AND starttime:[* TO "+dateFormat.format(nowdate)+"} AND  zl_ztID:[* TO *]";;
        String sqlzt="";

        //查询当日的展览
        try {
            resultSet=connection.executeSelect(Sources,sqlzl,0,10000,param);
            if (resultSet != null) {

                for (int i = 0; i < resultSet.size(); i++) {
                    resultSet.moveNext();
                    TRSRecord r=resultSet.get();
                    // System.out.println(r);
                    ExhibitionBean exhibitionBeanZL=new ExhibitionBean();
                    exhibitionBeanZL.setZl_name(r.getString("DOCTITLE"));
                    exhibitionBeanZL.setZllb(r.getString("zllb"));
                    exhibitionBeanZL.setQtxszqsj(r.getString("qtxszlsj"));
                    exhibitionBeanZL.setStarttime(r.getString("starttime"));
                    exhibitionBeanZL.setEndtime(r.getString("endtime"));
                    exhibitionBeanZL.setZldd(r.getString("zldd"));
                    exhibitionBeanZL.setZbdw(r.getString("zbdw"));
                    exhibitionBeanZL.setCbdw(r.getString("cbdw"));
                    exhibitionBeanZL.setZcdw(r.getString("zcdw"));
                    exhibitionBeanZL.setZltt(r.getString("zltt"));
                    exhibitionBeanZL.setZlhb(r.getString("zlhb"));
                    exhibitionBeanZL.setZljs(r.getString("zljs"));
                    exhibitionBeanZL.setZl_ztID(r.getString("zl_ztID"));
                    exhibitionBeanZL.setQtxszqsj(r.getString("qtxszq"));
                    exhibitionBeanZL.setId(r.getString("ID"));
                    exhibitionBeansZL.add(exhibitionBeanZL);
                }
            }
        } catch (TRSException e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                connection.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
        String zlztids="";
        //获取展览对应展厅的id
        for (ExhibitionBean exhibitionBean : exhibitionBeansZL ) {

            if(exhibitionBean.getZl_ztID().indexOf(",")!=-1){
                String[] zlztid= exhibitionBean.getZl_ztID().split(",");

                for(int i=0;i<zlztid.length;i++)
                        zlztids+=zlztid[i]+",";

            }
            else{
                zlztids+=exhibitionBean.getZl_ztID()+",";
            }

        }
        // 拼接展厅sql
        zlztids=zlztids.substring(0,zlztids.length()-1);
        String [] zlztidfg=zlztids.split(",");
        //展厅数量
        int ztsum=zlztidfg.length;
        for (int i=0;i<zlztidfg.length;i++){
            System.out.println( zlztidfg[i]);

            if(i==zlztidfg.length-1){
                sqlzt+="ID:"+zlztidfg[i];
            }else{
                sqlzt+="ID:"+zlztidfg[i]+" OR ";
            }
        }

        //查询对应的展厅
        try {
            resultSet=connection.executeSelect(Sources,sqlzt,0,10000,param);
            if (resultSet != null) {

                for (int i = 0; i < resultSet.size(); i++) {
                    resultSet.moveNext();
                    TRSRecord r=resultSet.get();
                    // System.out.println(r);
                    ExhibitionBean exhibitionBeanZT=new ExhibitionBean();
                    exhibitionBeanZT.setZt_name(r.getString("DOCTITLE"));
                    exhibitionBeanZT.setId(r.getString("ID"));
                    exhibitionBeansZT.add(exhibitionBeanZT);
                }
            }
        } catch (TRSException e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                connection.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }



        for (int i=0;i<exhibitionBeansZL.size();i++) {

             for (int j=0;j<exhibitionBeansZT.size();j++) {
                if(exhibitionBeansZL.get(i).getZl_ztID().indexOf(",")!=-1){
                    String[] zlzt=exhibitionBeansZL.get(i).getZl_ztID().split(",");

                    for (int h=0;h<zlzt.length;h++){
                        exhibitionBeansZT.get(j).getId();
                        if(zlzt[h].equals(exhibitionBeansZT.get(j).getId())){
                            if(exhibitionBeansZL.get(i).getZt_name()==""){
                                exhibitionBeansZL.get(i).setZt_name(exhibitionBeansZT.get(j).getZt_name());


                                ExhibitionBean exhibitionBeanqr=new ExhibitionBean();

                                exhibitionBeanqr.setZt_name(exhibitionBeansZT.get(j).getZt_name());
                                exhibitionBeanqr.setZl_name(exhibitionBeansZL.get(i).getZl_name());
                                exhibitionBeanqr.setZlhb(exhibitionBeansZL.get(i).getZlhb());
                                exhibitionBeanqr.setZldd(exhibitionBeansZL.get(i).getZldd());
                                exhibitionBeanqr.setZljs(exhibitionBeansZL.get(i).getZljs());
                                exhibitionBeanqr.setZltt(exhibitionBeansZL.get(i).getZltt());
                                exhibitionBeanqr.setQtxszqsj(exhibitionBeansZL.get(i).getQtxszqsj());
                                exhibitionBeanRequest.add(exhibitionBeanqr);


                            }
                            else{
                                exhibitionBeansZL.get(i).setZt_name(exhibitionBeansZL.get(i).getZt_name()+","+exhibitionBeansZT.get(j).getZt_name());

                                ExhibitionBean exhibitionBeanqr=new ExhibitionBean();

                                exhibitionBeanqr.setZt_name(exhibitionBeansZT.get(j).getZt_name());
                                exhibitionBeanqr.setZl_name(exhibitionBeansZL.get(i).getZl_name());
                                exhibitionBeanqr.setZlhb(exhibitionBeansZL.get(i).getZlhb());
                                exhibitionBeanqr.setZldd(exhibitionBeansZL.get(i).getZldd());
                                exhibitionBeanqr.setZljs(exhibitionBeansZL.get(i).getZljs());
                                exhibitionBeanqr.setZltt(exhibitionBeansZL.get(i).getZltt());
                                exhibitionBeanqr.setQtxszqsj(exhibitionBeansZL.get(i).getQtxszqsj());

                                exhibitionBeanRequest.add(exhibitionBeanqr);


                            }

                        }
                    }

                }else{
                    if(exhibitionBeansZL.get(i).getZl_ztID().equals(exhibitionBeansZT.get(j).getId())){
                    exhibitionBeansZL.get(i).setZt_name(exhibitionBeansZT.get(j).getZt_name());

                    ExhibitionBean exhibitionBeanqr=new ExhibitionBean();

                        exhibitionBeanqr.setZt_name(exhibitionBeansZT.get(j).getZt_name());
                        exhibitionBeanqr.setZl_name(exhibitionBeansZL.get(i).getZl_name());
                        exhibitionBeanqr.setZlhb(exhibitionBeansZL.get(i).getZlhb());
                        exhibitionBeanqr.setZldd(exhibitionBeansZL.get(i).getZldd());
                        exhibitionBeanqr.setZljs(exhibitionBeansZL.get(i).getZljs());
                        exhibitionBeanqr.setZltt(exhibitionBeansZL.get(i).getZltt());
                        exhibitionBeanqr.setQtxszqsj(exhibitionBeansZL.get(i).getQtxszqsj());

                        exhibitionBeanRequest.add(exhibitionBeanqr);

                    }
                }

            }

        }


        for (int i=0;i<exhibitionBeanRequest.size();i++){
            System.out.println(exhibitionBeanRequest.get(i).getQtxszqsj());
        }

        System.out.println("展览sql："+sqlzl);
        System.out.println("展厅sql："+sqlzt);

        Map<String, Object> map = new HashMap<>();
        map.put("exhibitionBeans",exhibitionBeanRequest);
        return map;
    }

    public static List<OtherZlBean> ShowZLUtil(TRSConnection connection,String id,Integer type) {


        //根据id 查询展览
        //获取当前日期
        Date nowdate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        GbUtil gbUtil = new GbUtil();
        String Sources = gbUtil.getVlaue("Sources");
        String Sources_stzj = gbUtil.getVlaue("Sources_ztsj");
        List<OtherZlBean> Ozlist = new ArrayList<>();
        String sql = "KGSJ:" + dateFormat.format(nowdate) + " OR BGSJ:" + dateFormat.format(nowdate);
        String flag_kb = "0";//0:初始值，1：开馆 ，2：闭馆
        try {
            //开闭馆时间获取
            resultSet = connection.executeSelect("kbsj", sql, 0, 10000, param);
            Date date = new Date();
            SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
            System.out.println(dateFm.format(date));
            if (resultSet != null) {
                for (int i = 0; i < resultSet.size(); i++) {
                    resultSet.moveNext();
                    TRSRecord r = resultSet.get();
                    Date kgsj= dateFormat.parse((r.getString("KGSJ")).toString().replace("/","-"));
                    Date bgsj= dateFormat.parse((r.getString("BGSJ")).toString().replace("/","-"));
                    if ((dateFormat.format(nowdate)).equals(dateFormat.format(kgsj))){
                        flag_kb = "1";
                    }
                    if  ((dateFormat.format(nowdate)).equals(dateFormat.format(bgsj))){
                        flag_kb = "2";
                    }
                }
            }
            if (("星期一".equals(dateFm.format(date))||"Monday".equals(dateFm.format(date))) && "0".equals(flag_kb)) {
                flag_kb = "2";
            }else if("0".equals(flag_kb)){
                flag_kb = "1";
            }
        } catch (TRSException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
        String sqlzl = "endtime:[" + dateFormat.format(nowdate) + " TO  *} AND starttime:[* TO " + dateFormat.format(nowdate) + "} AND  zl_ztID:" + id;
        if (type == 1) {
            sqlzl = "endtime:[" + dateFormat.format(nowdate) + " TO  *} AND starttime:[* TO " + dateFormat.format(nowdate) + "} AND  zl_ztID:[* TO *]";
        } else if (type == 3) {
            sqlzl = "starttime:[" + dateFormat.format(nowdate) + " TO *} AND  zl_ztID:[* TO *]";
        }
        String zlztids = "";
        String zltpids = "";
        //查询当日的展览并获取展览主题id
        if ("1".equals(flag_kb)) {
            try {


                resultSet = connection.executeSelect(Sources, sqlzl, 0, 10000, param);
                if (resultSet != null) {
                    for (int i = 0; i < resultSet.size(); i++) {
                        resultSet.moveNext();
                        TRSRecord r = resultSet.get();
                        // System.out.println(r);
                        OtherZlBean otherZlBean = new OtherZlBean();
                        if (!("").equals(r.getString("DOCTITLE"))) {
                            otherZlBean.setExhi_title(r.getString("DOCTITLE"));
                        } else {
                            otherZlBean.setExhi_title("Null");
                        }

                        if (!("").equals(r.getString("zllb_name"))) {
                            otherZlBean.setType(r.getString("zllb_name"));
                        } else {
                            otherZlBean.setType("Null");
                        }

                        if (!("").equals(r.getString("qtxszq"))) {
                            otherZlBean.setDate(r.getString("qtxszq"));
                        } else {
                            otherZlBean.setDate("Null");
                        }

                        if (!("").equals(r.getString("starttime"))) {
                            otherZlBean.setStart_time(r.getString("starttime"));
                        } else {
                            otherZlBean.setStart_time("Null");
                        }

                        if (!("").equals(r.getString("endtime"))) {
                            otherZlBean.setEnd_time(r.getString("endtime"));
                        } else {
                            otherZlBean.setEnd_time("Null");
                        }

                        if (!("").equals(r.getString("zldd"))) {
                            otherZlBean.setVenue(r.getString("zldd"));
                        } else {
                            otherZlBean.setVenue("Null");
                        }

                        if (!("").equals(r.getString("zbdw"))) {
                            otherZlBean.setHost(r.getString("zbdw"));
                        } else {
                            otherZlBean.setHost("Null");
                        }

                        if (!("").equals(r.getString("cbdw"))) {
                            otherZlBean.setOrganizer(r.getString("cbdw"));
                        } else {
                            otherZlBean.setOrganizer("Null");
                        }

                        if (!("").equals(r.getString("zltt"))) {
                            otherZlBean.setHor_poster(r.getString("zltt"));
                        } else {
                            otherZlBean.setHor_poster("Null");
                        }

                        if (!("").equals(r.getString("zlhb"))) {
                            otherZlBean.setVer_poster(r.getString("zlhb"));
                        } else {
                            otherZlBean.setVer_poster("Null");
                        }

                        if (!("").equals(r.getString("zljs"))) {
                            otherZlBean.setDescr(r.getString("zljs"));
                        } else {
                            otherZlBean.setDescr("Null");
                        }

                        if (!("").equals(r.getString("ID"))) {
                            otherZlBean.setId(r.getString("ID"));
                        } else {
                            otherZlBean.setId("Null");
                        }

                        if (i == resultSet.size() - 1) {
                            zlztids += "cp_zlID:" + r.getString("ID");
                            zltpids += "ID:" + r.getString("ID");
                        } else {
                            zlztids += "cp_zlID:" + r.getString("ID") + " OR ";
                            zltpids += "ID:" + r.getString("ID") + " OR ";
                        }
                        Ozlist.add(otherZlBean);
                    }
                }
            } catch (TRSException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            }

            //获取展览ID查询组装展览数据
            if (type == 2) {
                Map<String, List<Highlights>> Hmap = new HashMap<String, List<Highlights>>();
                List<Highlights> hList = new ArrayList<>();
                Map<String, List<Scenephotos>> Smap = new HashMap<String, List<Scenephotos>>();
                List<Scenephotos> SList = new ArrayList<>();
                //param.setSortMethod("-cp_zlID");
                boolean flag = false;
                String sqlcp = zlztids;
                ;
                try {
                    resultSet = connection.executeSelect(Sources, sqlcp, 0, 10000, param);
                    if (resultSet != null) {
                        for (int i = 0; i < resultSet.size(); i++) {
                            resultSet.moveNext();
                            flag = false;
                            TRSRecord r = resultSet.get();
                            hList = new ArrayList<>();
                            // System.out.println(r);
                            Highlights highlights = new Highlights();
                            if (!("").equals(r.getString("wwms"))) {
                                highlights.setHighlights_intro(r.getString("wwms"));
                            } else {
                                highlights.setHighlights_intro("Null");
                            }
                            if (!("").equals(r.getString("wwms"))) {
                                highlights.setHighlights_name(r.getString("DOCTITLE"));
                            } else {
                                highlights.setHighlights_name("Null");
                            }
                            if (!("").equals(r.getString("wwms"))) {
                                highlights.setHigh_image(r.getString("slt"));
                            } else {
                                highlights.setHigh_image("Null");
                            }

                            flag = Hmap.containsKey(r.getString("cp_zlID"));
                            if (flag) {
                                Hmap.get(r.getString("cp_zlID")).add(highlights);
                            } else {
                                hList.add(highlights);
                                Hmap.put(r.getString("cp_zlID"), hList);
                            }
                        }
                    }
                    //查询展厅图片
                    resultSet = connection.executeSelect(Sources_stzj, zltpids, 0, 10000, param);

                    if (resultSet != null) {
                        for (int i = 0; i < resultSet.size(); i++) {
                            resultSet.moveNext();
                            flag = false;
                            TRSRecord r = resultSet.get();
                            SList = new ArrayList<>();
                            // System.out.println(r);
                            if (r.getString("zlzttptp").indexOf(";") > 0) {
                                String[] ImageArr = r.getString("zlzttptp").split(";");
                                String[] IntroArr = r.getString("zlzttpmc").split(";");
                                for (int j = 0; j < ImageArr.length; j++) {
                                    Scenephotos scenephotos = new Scenephotos();
                                    scenephotos.setScenephotosImage(ImageArr[j]);
                                    scenephotos.setScenephotosIntro(IntroArr[j]);
                                    SList.add(scenephotos);
                                }

                            } else {
                                Scenephotos scenephotos = new Scenephotos();
                                if (!("").equals(r.getString("wwms"))) {
                                    scenephotos.setScenephotosImage(r.getString("wwms"));
                                } else {
                                    scenephotos.setScenephotosImage("Null");
                                }

                                if (!("").equals(r.getString("wwms"))) {
                                    scenephotos.setScenephotosIntro(r.getString("DOCTITLE"));
                                } else {
                                    scenephotos.setScenephotosIntro("Null");
                                }

                                SList.add(scenephotos);
                            }
                            Smap.put(r.getString("ID"), SList);

                       /* flag = Smap.containsKey (r.getString ("cp_zlID"));
                        if (flag) {
                            Hmap.get (r.getString ("ID")).add (highlights);
                        } else {
                            hList.add (highlights);
                            Hmap.put (r.getString ("ID"), hList);
                        }*/
                        }
                    }
                } catch (TRSException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.close();
                    }
                    if (resultSet != null) {
                        resultSet.close();
                    }
                }

                //组装json数据；置空数据
                List<Highlights> hListNull = new ArrayList<>();
                Highlights highlights = new Highlights();
                highlights.setHighlights_intro("Null");
                highlights.setHighlights_name("Null");
                highlights.setHigh_image("Null");
                hListNull.add(highlights);

                List<Scenephotos> SListNull = new ArrayList<>();
                Scenephotos scenephotos = new Scenephotos();
                scenephotos.setScenephotosImage("Null");
                scenephotos.setScenephotosIntro("Null");
                SListNull.add(scenephotos);
                for (int i = 0; i < Ozlist.size(); i++) {
                    flag = false;
                    flag = Hmap.containsKey(Ozlist.get(i).getId());
                    if (flag) {
                        Ozlist.get(i).setHighlights(Hmap.get(Ozlist.get(i).getId()));
                    } else {
                        Ozlist.get(i).setHighlights(hListNull);
                    }

                    flag = Smap.containsKey(Ozlist.get(i).getId());
                    if (flag) {
                        Ozlist.get(i).setScenephotos(Smap.get(Ozlist.get(i).getId()));
                    } else {
                        Ozlist.get(i).setScenephotos(SListNull);
                    }
                }
            }
            return Ozlist;
        }else{
            return null;
        }

    }
}
