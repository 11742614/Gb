package com.trs.gb.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.trs.commons.hybase.*;
import com.trs.commons.lang.OffsetLimit;
import com.trs.commons.lang.PagedList;
import com.trs.commons.lang.utils.StringUtils;
import com.trs.gb.bean.AllBean;
import com.trs.gb.bean.JsonBean;
import com.trs.gb.bean.QwjsBean;
import com.trs.gb.bean.SearchLog;
import com.trs.hybase.client.*;
import com.trs.hybase.client.params.ConnectParams;
import com.trs.hybase.client.params.SearchParams;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static javax.swing.UIManager.get;

public class GbUtil {


    private static TRSConnection connection = null;
    private static ConnectParams params = null;
    private static SearchParams param = new SearchParams();
    private static TRSResultSet resultSet = null;
    private static HybaseTemplate hybaseTemplate;

    static {
        hybaseTemplate = SpringContextUtil.getBean("hybaseTemplate", HybaseTemplate.class);
    }

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

    //根据channelid查询所有数据
    public Map<String, Object> searchAllByChannelid(Integer pagenum, Integer pageSize, String sql, String SortOrder, TRSConnection connection, Integer cutsize) {
        //共多少条
        Long sum = 0L;
        long t1 = 0L;
        long t2 = 0L;
        List<AllBean> allBeans = new ArrayList<>();

        String Sources = getVlaue("Sources");
        int pageStart = (pagenum - 1) * pageSize;
        PagedList<AllBean> sources = (PagedList<AllBean>) hybaseTemplate.search(createConnectParams(), getVlaue("Sources"), "allBean.All", sql, SortOrder, 0, OffsetLimit.fromPage(pagenum, pageSize));

        Map<String, Object> map = new HashMap<>();
        map.put("success", "success");
        map.put("message", "");
        map.put("result", sources);

        return map;
    }

    //时间转换时间戳
    public static Long dateToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        return date.getTime();


    }


    //开闭馆时间筛选
    public List ShowKBsj(TRSConnection connection) {
        List kgsj = new ArrayList();
        List bgsj = new ArrayList();
        List newtime = new ArrayList();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        String last = format.format(ca.getTime());

        Calendar cal_1 = Calendar.getInstance();//获取当前日期
        cal_1.add(Calendar.MONTH, 0);
        cal_1.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        String firstDay = format.format(cal_1.getTime());
        String sql = "KGSJ:[" + firstDay + " TO " + last + "} OR BGSJ:[" + firstDay + " TO " + last + "}";

        //查询开馆闭关时间
        try {
            resultSet = connection.executeSelect("kbsj", sql, 0, 10000, param);
            if (resultSet != null) {

                for (int i = 0; i < resultSet.size(); i++) {
                    resultSet.moveNext();
                    TRSRecord r = resultSet.get();
                    // System.out.println(r);
                    if (r.getString("KGSJ") != null && r.getString("KGSJ") != "")
                        kgsj.add(r.getString("KGSJ").substring(0, 10));
                    if (r.getString("BGSJ") != null && r.getString("BGSJ") != "")
                        bgsj.add(r.getString("BGSJ").substring(0, 10));

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

        for (int i = 0; i < Integer.parseInt(last.substring(8, 10)); i++) {

            if (i < 9) {
                Date today = new Date(last.substring(0, 8) + "0" + (i + 1));
                Calendar c = Calendar.getInstance();
                c.setTime(today);
                int weekday = c.get(Calendar.DAY_OF_WEEK);
                if (weekday != 2) {
                    //日期
                    String day = last.substring(0, 8) + "0" + (i + 1);

                    //判断闭馆时间
                    if (bgsj.size() != 0) {
                        for (int j = 0; j < bgsj.size(); j++) {
                            if (!bgsj.get(j).equals(day)) {

                                newtime.add(day);
                            }
                        }
                    } else {
                        newtime.add(day);
                    }

                }

            } else {

                Date today = new Date(last.substring(0, 8) + (i + 1));
                Calendar c = Calendar.getInstance();
                c.setTime(today);
                int weekday = c.get(Calendar.DAY_OF_WEEK);
                if (weekday != 2) {
                    String day = last.substring(0, 8) + (i + 1);
                    //判断闭馆时间
                    if (bgsj.size() != 0) {
                        for (int j = 0; j < bgsj.size(); j++) {
                            if (!bgsj.get(j).equals(day)) {

                                newtime.add(day);
                            }
                        }
                    } else {
                        newtime.add(day);
                    }
                }
            }

        }

        for (int i = 0; i < kgsj.size(); i++) {

            newtime.add(kgsj.get(i));

        }

        //去除重复日期
        for (int i = 0; i < newtime.size() - 1; i++) {
            for (int j = newtime.size() - 1; j > i; j--) {
                if (newtime.get(j).equals(newtime.get(i))) {
                    newtime.remove(j);
                }
            }
        }

        return newtime;
    }


    //获取某一天的展览
    public Map<String, Object> ShowOneTime(TRSConnection connection, String chnls, String time, JsonBean params) {
        String[] chnlses = chnls.split(",");
        String Sources = getVlaue("Sources");
        List<JsonBean> jsonBeans = new ArrayList<>();
        param.setSortMethod("+zl_sort");
        String sql = "";
        String tsql = " (endtime:[" + time + " TO  *} AND starttime:[* TO " + time + " ]) ";
        String csql = "";
        sql += tsql;
        String zllbsql = "";
        System.out.println(params.getZllb());
        if (params.getZllb() != null && params.getZllb().trim() != "") {
            if (params.getZllb().indexOf(",") > -1) {
                String[] zllbs = params.getZllb().split(",");

                for (int i = 0; i < zllbs.length; i++) {

                    if (i == zllbs.length - 1) {
                        zllbsql += "  zllb:" + zllbs[i];
                    } else {
                        zllbsql += "  zllb:" + zllbs[i] + " OR ";
                    }
                }

            } else {

                zllbsql += " zllb:" + params.getZllb() + " ";
            }

            sql += " AND (" + zllbsql + ")";
        }

        if (chnlses != null) {
            for (int i = 0; i < chnlses.length; i++) {
                if (i == (chnlses.length - 1)) {
                    csql += "chnls:" + chnlses[i];
                } else {
                    csql += "chnls:" + chnlses[i] + " OR ";
                }
            }
        }

        sql += " AND (" + csql + ") ";
        System.out.println("获取某天的：" + sql);
        //获取通常
        try {
            resultSet = connection.executeSelect(Sources, sql, 0, 10000, param);
            if (resultSet != null) {

                for (int i = 0; i < resultSet.size(); i++) {
                    resultSet.moveNext();
                    TRSRecord r = resultSet.get();
                    // System.out.println(r);
                    JsonBean jsonBean = new JsonBean();
                    jsonBean.setChnls(r.getString("chnls"));
                    jsonBean.setDOCTITLE(r.getString("DOCTITLE"));
                    jsonBean.setDOCPUBURL(r.getString("DOCPUBURL"));
                    jsonBean.setDOCCONTENT(r.getString("DOCCONTENT"));
                    jsonBean.setZldd(r.getString("zldd"));
                    jsonBean.setZltt(r.getString("zltt"));
                    jsonBean.setStarttime(r.getString("starttime"));
                    jsonBean.setEndtime(r.getString("endtime"));
                    jsonBean.setZlhb(r.getString("zlhb"));
                    jsonBean.setZljs(r.getString("zljs"));
                    jsonBean.setZllb(r.getString("zllb"));
                    jsonBean.setZtzlurl(r.getString("ztzlurl"));
                    jsonBean.setZl_sort(r.getString("zl_sort"));
                    jsonBeans.add(jsonBean);
                }

            }

            System.out.println(sql);
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

        Map<String, Object> map = new HashMap<>();
        map.put("jsonBeans", jsonBeans);
        map.put("sum", resultSet.getNumFound());
        param.setSortMethod("");
        return map;
    }


    //获取某一天的活动
    public Map<String, Object> ShowOneTimeHD(TRSConnection connection, String chnls, String time, JsonBean params) {
        String[] chnlses = chnls.split(",");
        String Sources = getVlaue("Sources");
        List<JsonBean> jsonBeans = new ArrayList<>();
        String sql = "";
        String tsql = "hd_endtime:[" + time + "  TO  *} AND hd_starttime:[* TO " + time + " ]";
        String csql = "";
        sql += tsql;

        if (params.getHdlb() != null && params.getHdlb() != "") {
            sql += "  AND hd_lb:" + params.getHdlb();
        }


        if (chnlses != null) {
            for (int i = 0; i < chnlses.length; i++) {
                if (i == (chnlses.length - 1)) {
                    csql += "chnls:" + chnlses[i];
                } else {
                    csql += "chnls:" + chnlses[i] + " OR ";
                }
            }
        }

        sql += "AND (" + csql + ") ";
        System.out.println("获取某天的活动：" + sql);
        try {
            resultSet = connection.executeSelect(Sources, sql, 0, 10000, param);
            if (resultSet != null) {

                for (int i = 0; i < resultSet.size(); i++) {
                    resultSet.moveNext();
                    TRSRecord r = resultSet.get();
                    // System.out.println(r);
                    JsonBean jsonBean = new JsonBean();
                    jsonBean.setChnls(r.getString("chnls"));
                    jsonBean.setDOCTITLE(r.getString("DOCTITLE"));
                    jsonBean.setDOCPUBURL(r.getString("DOCPUBURL"));
                    jsonBean.setDOCCONTENT(r.getString("DOCCONTENT"));
                    jsonBean.setZldd(r.getString("zldd"));
                    jsonBean.setZltt(r.getString("zltt"));
                    jsonBean.setStarttime(r.getString("starttime"));
                    jsonBean.setEndtime(r.getString("endtime"));
                    jsonBean.setZlhb(r.getString("zlhb"));
                    jsonBean.setZljs(r.getString("zljs"));
                    jsonBean.setZllb(r.getString("zllb"));
                    jsonBean.setZtzlurl(r.getString("ztzlurl"));
                    jsonBean.setDOCRELTIME(r.getString("DOCRELTIME"));
                    jsonBeans.add(jsonBean);
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

        Map<String, Object> map = new HashMap<>();
        map.put("jsonBeans", jsonBeans);
        map.put("sum", resultSet.getNumFound());
        return map;
    }


    //获取某一段时间的的展览回顾
    public Map<String, Object> Showzlhg(TRSConnection connection, String chnls, String starttime, String endtime, JsonBean params, Integer pagenum, Integer pageSize, String searchword) {
        param.setSortMethod("+zl_sort");
        String[] chnlses = chnls.split(",");
        String Sources = getVlaue("Sources");
        List<JsonBean> jsonBeans = new ArrayList<>();
        String sql = "";
        String tsql = " ((endtime:[" + endtime + " TO * } AND starttime:[ *  TO " + starttime + " }) OR endtime:[" + starttime + "  TO " + endtime + " } OR starttime:[" + starttime + " TO " + endtime + " })";
        String csql = "";
        sql += tsql;


        int pageStart = (pagenum - 1) * pageSize;
        if (params.getZllb() != null && params.getZllb().trim() != "") {
            sql += " AND zllb:" + params.getZllb() + " ";
        }

        if (chnlses != null) {

            for (int i = 0; i < chnlses.length; i++) {
                if (i == (chnlses.length - 1)) {
                    csql += "chnls:" + chnlses[i];
                } else {
                    csql += "chnls:" + chnlses[i] + " OR ";
                }
            }
        }
        sql += "AND (" + csql + ") ";


        if (searchword != "" && searchword != null) {
            sql += "AND  DOCTITLE:" + searchword;
        } else {
            sql += "AND  DOCTITLE:[* TO *]";
        }

        try {
            resultSet = connection.executeSelect(Sources, sql, pageStart, pageSize, param);
            if (resultSet != null) {

                for (int i = 0; i < resultSet.size(); i++) {
                    resultSet.moveNext();
                    TRSRecord r = resultSet.get();
                    // System.out.println(r);
                    JsonBean jsonBean = new JsonBean();
                    jsonBean.setChnls(r.getString("chnls"));
                    jsonBean.setDOCTITLE(r.getString("DOCTITLE"));
                    jsonBean.setDOCPUBURL(r.getString("DOCPUBURL"));
                    jsonBean.setDOCCONTENT(r.getString("DOCCONTENT"));
                    jsonBean.setZldd(r.getString("zldd"));
                    jsonBean.setZltt(r.getString("zltt"));
                    jsonBean.setStarttime(r.getString("starttime"));
                    jsonBean.setEndtime(r.getString("endtime"));
                    jsonBean.setZlhb(r.getString("zlhb"));
                    jsonBean.setZljs(r.getString("zljs"));
                    jsonBean.setZllb(r.getString("zllb"));
                    jsonBean.setZl_sort(r.getString("zl_sort"));
                    jsonBean.setQtxszq(r.getString("qtxszq"));
                    jsonBeans.add(jsonBean);
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

        System.out.println("sql" + sql);
        Map<String, Object> map = new HashMap<>();
        map.put("jsonBeans", jsonBeans);
        map.put("sum", resultSet.getNumFound());
        param.setSortMethod("");
        return map;
    }


    //获得连接
    public TRSConnection getConnection() {

        //获取配置信息
        String serverList = getVlaue("serverList");
        String useID = getVlaue("useID");
        String password = getVlaue("password");

        //创建连接对象
        connection = new TRSConnection(serverList, useID, password, params);

        return connection;

    }

    //获取连接
    private HybaseConnectParams createConnectParams() {
        HybaseConnectParams connectParams = new HybaseConnectParams(getVlaue("serverList"),
                getVlaue("useID"), getVlaue("password"), 0L, 0L);
        return connectParams;
    }


    //返回某月标记时间
    public Map<String, Object> ShowTimeJson(TRSConnection connection, String starttime, String endtime, String chnls) {

        GbUtil gbUtil = new GbUtil();
        List list = gbUtil.ShowKBsj(gbUtil.getConnection());
        //最后生成的list
        List newlist = new ArrayList();
        connection = getConnection();
        Map<String, Object> map = new HashMap<>();
        Long starttimefromSources = 0L;
        Long endtimefromSources = 0L;
        String Sources = getVlaue("Sources");
        String[] chnlses = chnls.split(",");
        int day = Integer.parseInt(endtime.substring(8, 10));
        String sql = "";
        String tsql = "((starttime:[* TO " + starttime + "} AND endtime:[" + endtime + " TO *}) OR (starttime:[" + starttime + " TO " + endtime + "} AND endtime:[" + endtime + " TO *}) OR (starttime:[* TO " + starttime + "} AND endtime:[" + starttime + " TO " + endtime + "}))";
        String csql = "";
        sql += tsql;

        //栏目id条件获取
        if (chnlses != null) {

            for (int i = 0; i < chnlses.length; i++) {
                if (i == (chnlses.length - 1)) {
                    csql += "chnls:" + chnlses[i];
                } else {
                    csql += "chnls:" + chnlses[i] + " OR ";
                }
            }
        }
        sql += "AND (" + csql + ") ";
        JsonBean jsonBean = new JsonBean();
        List<Long> stime = new ArrayList<Long>();
        List<Long> etime = new ArrayList<Long>();

        try {
            SearchParams param = new SearchParams();
            resultSet = connection.executeSelect(Sources, sql, 0, 10000, param);

            if (resultSet != null) {

                for (int i = 0; i < resultSet.size(); i++) {
                    resultSet.moveNext();
                    TRSRecord r = resultSet.get();
                    try {
                        starttimefromSources = dateToStamp(r.getString("starttime"));
                        endtimefromSources = dateToStamp(r.getString("endtime"));
                        stime.add(starttimefromSources);
                        etime.add(endtimefromSources);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (TRSException e) {
            System.out.println("json查找失败：" + e.getErrorCode() + e.getErrorString());
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }

        String jsontext = "";
        Long d = 0L;
        for (int i = 0; i < day; i++) {
            try {
                String s = starttime.substring(0, 8) + (i + 1) + " 00:00:00";
                d = dateToStamp(s);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for (int j = 0; j < resultSet.getNumFound(); j++) {
                for (int l = 0; l < list.size(); l++) {
                    if (stime.get(j) < d && d < etime.get(j)) {
                        if (i < 9) {
                            String selectday = starttime.substring(0, 8) + "0" + (i + 1);
                            if (selectday.equals(list.get(l))) {

                                newlist.add(selectday);
                                break;


                            }

                        } else {
                            String selectday = starttime.substring(0, 8) + (i + 1);
                            if (selectday.equals(list.get(l))) {

                                newlist.add(selectday);
                                break;
                            }

                        }

                    }

                }


            }
        }
        //去除重复
        for (int i = 0; i < newlist.size() - 1; i++) {
            for (int j = newlist.size() - 1; j > i; j--) {
                if (newlist.get(j).equals(newlist.get(i))) {
                    newlist.remove(j);
                }
            }
        }

        for (int i = 0; i < newlist.size(); i++) {

            map.put(newlist.get(i).toString(), "1");
        }

        return map;
    }


    //全文检索
    public Map<String, Object> ShowQwjs(Integer pagenum, Integer pageSize, String sql, String SortOrder, TRSConnection connection, Integer cutsize, boolean isApp) {
        //共多少条
        Long sum = 0L;
        long t1 = 0L;
        long t2 = 0L;
        List<QwjsBean> qwjsBeans = new ArrayList<>();
        //设置检索排序方式
        if (cutsize != null && cutsize != 0) {
            param.setCutSize(cutsize);
        }
        if (null != SortOrder) {
            param.setSortMethod(SortOrder);
        }
        String Sources = isApp ? getVlaue("AppSources") : getVlaue("Sources");
        System.out.println("库：" + Sources);
        int pageStart = (pagenum - 1) * pageSize;
        //查询总条数
       /* try {

            resultSet = connection.executeSelect(Sources, sql, pageStart, pageSize, param);
            sum = resultSet.getNumFound();
        } catch (TRSException e) {
            System.out.println("全文查找失败：" + e.getErrorCode() + e.getErrorString());
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }*/
//
        PagedList<QwjsBean> sources = (PagedList<QwjsBean>) hybaseTemplate.search(createConnectParams(), Sources, "guobo.All", sql, SortOrder, 0, OffsetLimit.fromPage(pagenum, pageSize));
        //查询详细数据
        /*try {
            t1 = System.currentTimeMillis();
            resultSet = connection.executeSelect(Sources, sql, pageStart, pageSize, param);


            if (resultSet != null) {

                for (int i = 0; i < resultSet.size(); i++) {
                    resultSet.moveNext();
                    TRSRecord r = resultSet.get();
                    // System.out.println(r);
                    QwjsBean qwjsBean = new QwjsBean();
                    qwjsBean.setChnls(r.getString("chnls"));
                    qwjsBean.setClassification(r.getString("classification"));
                    qwjsBean.setDOCTITLE(r.getString("DOCTITLE"));

                    qwjsBean.setDOCCONTENT(r.getString("DOCCONTENT"));

                    qwjsBean.setDOCRELTIME(r.getString("DOCRELTIME"));
                    qwjsBean.setDOCPUBURL(r.getString("DOCPUBURL"));
                    qwjsBean.setDOCHTMLCON(r.getString("DOCHTMLCON"));
                    qwjsBean.setGg(r.getString("gg"));
                    qwjsBean.setNd(r.getString("nd"));
                    qwjsBean.setSlt(r.getString("slt"));
                    qwjsBean.setIssue(r.getString("issue"));
                    qwjsBean.setRcontent(r.getString("rcontent"));
                    qwjsBean.setRtime(r.getString("rtime"));
                    qwjsBean.setIssue_name(r.getString("issue_name"));
                    qwjsBean.setCtime(r.getString("ctime"));
                    qwjsBeans.add(qwjsBean);
                }

            }

        } catch (TRSException e) {
            System.out.println("全文查找失败：" + e.getErrorCode() + e.getErrorString());
        } finally {
            t2 = System.currentTimeMillis();
            if (connection != null) {
                connection.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }*/

        Map<String, Object> map = new HashMap<>();
        map.put("qwjsBeans", sources.getPageData());
        map.put("sum", sources.getTotalRecords());
        map.put("pageNumber", pagenum);
        map.put("pageSize", pageSize);
        map.put("spendTime", t2 - t1);
        param.setSortMethod("");
        return map;
    }


    //分类查询
    public Map<String, Long> getChnlsNum(TRSConnection connection, String chnlsQuery, String chnls, boolean isApp) {

        String Sources = isApp ? getVlaue("AppSources") : getVlaue("Sources");
        System.out.println("库：" + Sources);
        String[] chnlses = chnls.split(",");
        List list = new ArrayList();
//        try {
        CategoryResult chnls1 = hybaseTemplate.searchCategory(createConnectParams(), Sources, "guobo.All", chnlsQuery, "chnls", Long.MAX_VALUE);
        Map<String, Long> categoryMap = chnls1.getCategoryMap();
        return categoryMap;
//            resultSet = connection.categoryQuery(Sources, chnlsQuery, "chnls", "chnls", Long.MAX_VALUE, param);
//
//            Map<String, Long> category = resultSet.getCategoryMap();
//            if (category == null) {
//                return null;
//            }
//            for (int i = 0; i < chnlses.length; i++) {
//                list.add(category.get(chnlses[i]));
//
//            }
//
//
//        } catch (TRSException e) {
//            System.out.println("分类查找失败：" + e.getErrorCode() + e.getErrorString());
//        } finally {
//            if (connection != null) {
//                connection.close();
//            }
//            if (resultSet != null) {
//                resultSet.close();
//            }
//        }

    }


    //全文检索返回的字段拼接
    public static String GetQwjsCondition(String SearchScope, String TimeScope, String SearchWord, String chnlslimit, String classification, String notchnls, String siteid, Integer notInclude) {
        List cons = new ArrayList();
       /* if ("[* TO *]".equals(SearchWord)) {
            SearchWord = "";
        }*/
        if (StringUtils.isNotEmpty(siteid)) {
            cons.add(HybaseUtils.eqCond("SITEID", siteid));
        }

        if (StringUtils.isNotEmpty(chnlslimit)) {
            cons.add(HybaseUtils.inCond("chnls", chnlslimit.split(",")));
        }
        if (StringUtils.isNotEmpty(notchnls)) {
            cons.add(HybaseUtils.notCond(HybaseUtils.inCond("chnls", false, true, notchnls.split(","))));
        }
        if (StringUtils.isNotEmpty(classification)) {
            cons.add(HybaseUtils.inCond("classification", "AND", false, false, Arrays.asList(classification.split(","))));
        }
        if (StringUtils.isNotEmpty(SearchScope) && StringUtils.isNotEmpty(SearchWord)) {
            if (notInclude == 1) {//查询不包含该词的数据
                SearchWord = "[* TO *] NOT " + SearchWord;
                if (SearchScope.equals("all")) {
                    cons.add(escapeReplace(HybaseUtils.andCond(HybaseUtils.eqCond("DOCTITLE", SearchWord), HybaseUtils.eqCond("DOCCONTENT", SearchWord))));
                } else {
                    cons.add(escapeReplace(HybaseUtils.eqCond(SearchScope, SearchWord)));
                }
            } else {
                boolean isFlag = false;
                if (SearchWord.contains("OR")) {
                    isFlag = true;
                }
                if (SearchScope.equals("all")) {
                    if (isFlag) {
                        cons.add(escapeReplace(HybaseUtils.orCond(HybaseUtils.eqCond("DOCTITLE", "(" + SearchWord + ")"), HybaseUtils.eqCond("DOCCONTENT", "(" + SearchWord + ")"))));
                    } else {
                        cons.add(escapeReplace(HybaseUtils.orCond(HybaseUtils.eqCond("DOCTITLE", SearchWord), HybaseUtils.eqCond("DOCCONTENT", SearchWord))));
                    }

                } else {
                    if (isFlag) {
                        cons.add(escapeReplace(HybaseUtils.eqCond(SearchScope, "(" + SearchWord + ")")));
                    } else {
                        cons.add(escapeReplace(HybaseUtils.eqCond(SearchScope, SearchWord)));
                    }

                }
            }


        }
        //检索时间范围
        System.out.println("TimeScope:" + TimeScope);
        if (null != TimeScope && TimeScope != "") {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String nowDate = format.format(new Date());
            String oldDate = "";
            Calendar c = Calendar.getInstance();
            TimeScope = TimeScope.replace(",", "");
            if ("month".equals(TimeScope)) {
                c.setTime(new Date());
                c.add(Calendar.MONTH, -1);
                Date m = c.getTime();
                oldDate = format.format(m);
            }
            if ("year".equals(TimeScope)) {
                c.setTime(new Date());
                c.add(Calendar.YEAR, -1);
                Date y = c.getTime();
                oldDate = format.format(y);
            }
            if ("week".equals(TimeScope)) {
                //过去七天
                c.setTime(new Date());
                c.add(Calendar.DATE, -7);
                Date d = c.getTime();
                oldDate = format.format(d);
            }
            if ("day".equals(TimeScope)) {
                //当天
                oldDate = nowDate.substring(0, 10);
            }
            cons.add(HybaseUtils.dateCond("DOCRELTIME", oldDate, nowDate.substring(0, 10)));
//            condition += " AND DOCRELTIME:[\"" + oldDate + "\" TO \"" + nowDate.substring(0, 10) + " 23:59:59 \"]";
        }

        System.out.println(HybaseUtils.andCond(cons));
        return HybaseUtils.andCond(cons);

    }

    //转义符替换
    public static String escapeReplace(String searchWord) {
        if (searchWord.contains("OR") || searchWord.contains("or") || searchWord.contains("\\[\\*\\ TO\\ \\*\\]\\ NOT\\")) {
            if (searchWord.contains("\\")) {
                searchWord = searchWord.replaceAll("\\\\", "");
            }
        }
        return searchWord;
    }

    //生成json
    public String toJson(List jsonBeans) {

        String jsontext = JSON.toJSONString(jsonBeans, SerializerFeature.DisableCircularReferenceDetect);


        return jsontext;
    }


    //获取某一段时间的的展览活动---------------------------------------
    public Map<String, Object> ShowSomeTimeZLHD(TRSConnection connection, String starttime, String endtime, JsonBean params) {
        String Sources = getVlaue("Sources");
        List<JsonBean> jsonBeans = new ArrayList<>();
        String sql = "";
        String tsql = " ((hd_endtime:[" + endtime + " TO * } AND hd_starttime:[ *  TO " + starttime + "}) OR hd_endtime:[" + starttime + " TO " + endtime + "} OR hd_starttime:[" + starttime + " TO " + endtime + "})";
        String csql = "";
        sql += tsql;

        try {
            resultSet = connection.executeSelect(Sources, sql, 0, 10000, param);
            if (resultSet != null) {

                for (int i = 0; i < resultSet.size(); i++) {
                    resultSet.moveNext();
                    TRSRecord r = resultSet.get();
                    // System.out.println(r);
                    JsonBean jsonBean = new JsonBean();
                    jsonBean.setChnls(r.getString("chnls"));
                    jsonBean.setDOCTITLE(r.getString("DOCTITLE"));
                    jsonBean.setDOCPUBURL(r.getString("DOCPUBURL"));
                    jsonBean.setDOCCONTENT(r.getString("DOCCONTENT"));
                    jsonBean.setZldd(r.getString("zldd"));
                    jsonBean.setZltt(r.getString("zltt"));
                    jsonBean.setStarttime(r.getString("starttime"));
                    jsonBean.setEndtime(r.getString("endtime"));
                    jsonBean.setZlhb(r.getString("zlhb"));
                    jsonBean.setZljs(r.getString("zljs"));
                    jsonBean.setZllb(r.getString("zllb"));
                    jsonBeans.add(jsonBean);
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

        System.out.println(sql);
        Map<String, Object> map = new HashMap<>();
        map.put("jsonBeans", jsonBeans);
        map.put("sum", resultSet.getNumFound());
        return map;
    }

    //记录检索日志
    public void insert(HttpServletRequest request,String wearchWord) {
        if(StringUtils.isNotBlank(wearchWord)){
            List<String> resultList = new ArrayList<>();
            String[] str = new String[0];
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(wearchWord.contains("OR")){
                if(wearchWord.contains("OR")){
                    str = wearchWord.split("OR");
                    resultList= new ArrayList<>(Arrays.asList(str));
                }
            }else {
                resultList.add(wearchWord);
            }
            for (String strs:resultList) {
                System.out.println("检索日志记录检索词为："+strs);
                String ip = IpUtils.getIpAddr(request);
                Date date = new Date();
                SearchLog searchLog = new SearchLog();
                searchLog.setIp(ip);
                searchLog.setSearchTime(format.format(date));
                searchLog.setSearchWord(strs.trim());
                String sources = getVlaue("Search_log");
                InsertOptions options = new InsertOptions();
                options.setDuplicateErrorHandle(InsertOptions.SKIP_ERROR);
                hybaseTemplate.insert(createConnectParams(),sources,"searchLog.All",searchLog,options);
            }

        }

       // hybaseTemplate.insert("searchLog.All", searchLog);
    }
    //获取导出所需要的数据
    public List<SearchLog> searchWordCategory() {
        List<SearchLog> list = new ArrayList<SearchLog>();
        String sources = getVlaue("Search_log");
        CategoryResult searchWordCate = hybaseTemplate.searchCategory(createConnectParams(), sources, "searchLog.All", "*:*", "searchWord", Long.MAX_VALUE);
        Map<String, Long> categoryMap = searchWordCate.getCategoryMap();
        int i=0;
        for (Map.Entry<String, Long> entry : categoryMap.entrySet()) {
            SearchLog searchLog = new SearchLog();
            searchLog.setId(++i);
            searchLog.setSearchWord(entry.getKey());
            searchLog.setCount(entry.getValue());
            list.add(searchLog);
        }
        return list;
    }
}
