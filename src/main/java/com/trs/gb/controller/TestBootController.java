package com.trs.gb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trs.commons.hybase.HybaseUtils;
import com.trs.commons.lang.utils.StringUtils;
import com.trs.gb.bean.*;
import com.trs.gb.tools.*;
import com.trs.hybase.client.TRSConnection;
import com.trs.hybase.client.TRSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@ComponentScan(basePackages = "com.netease.interceptor")
@RestController
@RequestMapping("")
public class TestBootController {
    @Autowired
    private RedisConfig redisConfig;
    //获取某月的哪天有展览
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/TimeJson", method = RequestMethod.GET)
    public String getJson(JsonBean jsonBean) {
        GbUtil tools = new GbUtil();
        Map<String,Object> map=new HashMap<>();
        map=  tools.ShowTimeJson(tools.getConnection(),jsonBean.getStarttime(),jsonBean.getEndtime(),jsonBean.getChnls());
        JSONObject json =new JSONObject(map);
        return json.toString();
    }

   //获取某一天的展览
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/getOneTime", method = RequestMethod.GET)
    public String getOneTime(JsonBean jsonBean) {
        GbUtil gbUtil = new GbUtil();

        List<JsonBean> searchAllBeans=new ArrayList<>();
        List list=gbUtil.ShowKBsj(gbUtil.getConnection());



        for (int i=0;i<list.size();i++){
            if(jsonBean.getTime().equals(list.get(i))){
                Map<String, Object> m = gbUtil.ShowOneTime(gbUtil.getConnection(),jsonBean.getChnls(),jsonBean.getTime(),jsonBean);
                searchAllBeans = (List<JsonBean>) m.get("jsonBeans");
            }
        }
        String jsontext=gbUtil.toJson(searchAllBeans);
        return jsontext;
    }


    //获取某一天的展览活动
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/getOneTimeHD", method = RequestMethod.GET)
    public String getOneTimeHD(JsonBean jsonBean) {
        GbUtil gbUtil = new GbUtil();
        Map<String, Object> m = gbUtil.ShowOneTimeHD(gbUtil.getConnection(),jsonBean.getChnls(),jsonBean.getTime(),jsonBean);
        List<JsonBean> searchAllBeans = (List<JsonBean>) m.get("jsonBeans");
        String jsontext=gbUtil.toJson(searchAllBeans);
        return jsontext;
    }





    //获取展览回顾
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/getZLHG", method = RequestMethod.GET)
    public String getZhanLanHuiGui(JsonBean jsonBean) {
        GbUtil gbUtil = new GbUtil();
        Map<String, Object> m = gbUtil.Showzlhg(gbUtil.getConnection(),jsonBean.getChnls(),jsonBean.getStarttime(),jsonBean.getEndtime(),jsonBean,jsonBean.getPageNum(),jsonBean.getPageSize(),jsonBean.getSearchword());
        List<JsonBean> searchAllBeans = (List<JsonBean>) m.get("jsonBeans");

        Long sum = (Long) m.get("sum");
        String jsonsum="{\"sum\":\""+sum+"\"}]";
        String jsontext = gbUtil.toJson(searchAllBeans).toString();
        jsontext=jsontext.replaceAll("]",",");
        jsontext+=jsonsum;

        return jsontext;
    }





    //全文检索
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/SearchAll", method = RequestMethod.GET)
    public String SearchAll(HttpServletRequest request, RequestBean requestBean, @RequestParam(value = "siteid",required = false,defaultValue = "5") String siteid,@RequestParam(value = "notInclude",required = false,defaultValue = "0")Integer notInclude) {
        GbUtil gbUtil = new GbUtil();
        TRSConnection con = gbUtil.getConnection();
        String sql = gbUtil.GetQwjsCondition(requestBean.getSearchScope(), requestBean.getTimeScope(), requestBean.getSearchWord(),requestBean.getChnls(),requestBean.getClassification(),requestBean.getNotchnls(),siteid, notInclude);
        if(sql.startsWith("(SITEID:5)")){
            sql = sql.substring(1,sql.length());
            sql = "(SRCSITEID:5 OR " + sql;
        }
        Map<String, Object> m = gbUtil.ShowQwjs(requestBean.getPageNum(),requestBean.getPageSize(), sql, requestBean.getSortOrder(), con,requestBean.getCutsize(),false);
        List<QwjsBean> searchAllBeans = (List)m.get("qwjsBeans");
        Long sum = (Long)m.get("sum");
        String jsontext = gbUtil.toJson(searchAllBeans).toString();
       //添加检索日志
        gbUtil.insert(request,requestBean.getSearchWord());
        return jsontext;
    }

    /**
     * 手机端检索
     * @param requestBean
     * @param siteid
     * @return
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/AppSearchAll", method = RequestMethod.GET)
    public String AppSearchAll(HttpServletRequest request, RequestBean requestBean, @RequestParam(value = "siteid",required = false,defaultValue = "10") String siteid,@RequestParam(value = "notInclude",required = false,defaultValue = "0")Integer notInclude) {

        GbUtil gbUtil = new GbUtil();
        TRSConnection con = gbUtil.getConnection();
        String sql = gbUtil.GetQwjsCondition(requestBean.getSearchScope(), requestBean.getTimeScope(), requestBean.getSearchWord(),requestBean.getChnls(),requestBean.getClassification(),requestBean.getNotchnls(),siteid,notInclude);
        Map<String, Object> m = gbUtil.ShowQwjs(requestBean.getPageNum(),requestBean.getPageSize(), sql, requestBean.getSortOrder(), con,requestBean.getCutsize(),true);
        List<QwjsBean> searchAllBeans = (List)m.get("qwjsBeans");
        Long sum = (Long)m.get("sum");
        String jsontext = gbUtil.toJson(searchAllBeans).toString();
        //添加检索日志
        gbUtil.insert(request,requestBean.getSearchWord());
        return jsontext;
    }

    /**
     * 微信分享
     * @param requestBean
     * @param url
     * @return
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/Share",method = RequestMethod.GET)
    @ResponseBody
    public Object testvoid(RequestBean requestBean,@RequestParam("url") String url){
        WinXinEntity winXinEntity = WeinXinUtil.getWinXinEntity(url,redisConfig);
        return winXinEntity;
    }

    //英文检索
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/EnglishSearch", method = RequestMethod.GET)
    @ResponseBody
    public Map Search(HttpServletRequest request, RequestBean requestBean, @RequestParam(value = "siteid",required = false,defaultValue = "9") String siteid,@RequestParam(value = "notInclude",required = false,defaultValue = "0")Integer notInclude) {
        GbUtil gbUtil = new GbUtil();
        TRSConnection con = gbUtil.getConnection();
        String sql = gbUtil.GetQwjsCondition(requestBean.getSearchScope(), requestBean.getTimeScope(), requestBean.getSearchWord(),requestBean.getChnls(),requestBean.getClassification(),requestBean.getNotchnls(),siteid, notInclude);
        Map<String, Object> m = gbUtil.ShowQwjs(requestBean.getPageNum(),requestBean.getPageSize(), sql, requestBean.getSortOrder(), con,requestBean.getCutsize(),false);
        //添加检索日志
        gbUtil.insert(request,requestBean.getSearchWord());
        return m;
    }






    //获取条数及时间
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/Getsum", method = RequestMethod.GET)
    public String Getsum(RequestBean requestBean,@RequestParam(value = "siteid",required = false) String siteid,@RequestParam(required = false) boolean isApp,@RequestParam(value = "notInclude",required = false,defaultValue = "0")Integer notInclude) {

        GbUtil gbUtil = new GbUtil();
        TRSConnection con = gbUtil.getConnection();
        String sql = gbUtil.GetQwjsCondition(requestBean.getSearchScope(), requestBean.getTimeScope(), requestBean.getSearchWord(),requestBean.getChnls(),requestBean.getClassification(),requestBean.getNotchnls(), siteid, notInclude);

        Map<String, Object> m = gbUtil.ShowQwjs(requestBean.getPageNum(),requestBean.getPageSize(), sql, requestBean.getSortOrder(), con,requestBean.getCutsize(),isApp);
        Long sum = (Long) m.get("sum");
        Long spendTime=(Long)m.get("spendTime");
        String jsontext="[{\"sum\":\""+sum+"\"},{\"spendTime\":\""+spendTime+"\"}]";
        return jsontext;
    }





    //chnls分类检索
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/Getchnlssort", method = RequestMethod.GET)
    public String Getchnlssort(RequestBean requestBean,@RequestParam(value = "siteid",required = false) String siteid,@RequestParam(required = false) boolean isApp,@RequestParam(value = "notInclude",required = false,defaultValue = "0")Integer notInclude){
        String jsontext="";
        GbUtil gbUtil = new GbUtil();
        List l=new ArrayList();
        TRSConnection con = gbUtil.getConnection();
        String sql = gbUtil.GetQwjsCondition(requestBean.getSearchScope(), requestBean.getTimeScope(), requestBean.getSearchWord(),requestBean.getChnlslimit(),requestBean.getClassification(),requestBean.getNotchnls(), siteid, notInclude);
//        List list =gbUtil.getChnlsNum(con,sql,requestBean.getChnls());
        Map<String,Long> list =gbUtil.getChnlsNum(con,sql,requestBean.getChnls(),isApp);
        if(list==null){
            return null;
        }JSONObject json = new JSONObject();

        String s="";
        String[] chnlses=requestBean.getChnls().split(",");
        for (int i = 0; i < chnlses.length; i++) {
            if(null!=list.get(chnlses[i])){
                json.put(chnlses[i],list.get(chnlses[i]));
            }
        }
        l.add(json.toJSONString());
//jsontext = gbUtil.toJson(l);
//jsontext=jsontext.replace("[","[{");
//jsontext=jsontext.replace(":","\":\"");
//jsontext=jsontext.replace("]","}]");
        return l.toString();
    }





    //获取当日的展厅展览
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/getExhibition", method = RequestMethod.GET)
    public String getExhibition(JsonBean jsonBean) {
        GbUtil gbUtil=new GbUtil();
        ExhibitionUtil exhibitionUtil=new ExhibitionUtil();
        Map<String, Object> m=exhibitionUtil.ShowExhibitionUtil(gbUtil.getConnection());

        List<JsonBean> exhibitionBeans = (List<JsonBean>) m.get("exhibitionBeans");
        String jsontext=gbUtil.toJson(exhibitionBeans);
        return jsontext;
    }

    //根据展厅id获取当天展厅及藏品
    @RequestLimit(count=600, time=86400)
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/exhibitionDetail", method = RequestMethod.GET)
    public String getExhibitionDetail(HttpServletRequest request, HttpServletResponse response) {
        long startTime=System.currentTimeMillis();
        GbUtil gbUtil=new GbUtil();
        ExhibitionUtil exhibitionUtil=new ExhibitionUtil();
        String id=request.getParameter("id");
        System.out.println("开始执行"+id);
        List<OtherZlBean> list=exhibitionUtil.ShowZLUtil(gbUtil.getConnection(),id,2);
        //List<JsonBean> exhibitionBeans = (List<JsonBean>) m.get("exhibitionBeans");
        String jsontext = getString(startTime, gbUtil, list);
        return jsontext;
    }




    //根据当天展厅展览
   // @RequestLimit(count=6, time=86400)
    @RequestLimit(count=10,time=600)
    @RequestMapping(value = "/currentExhibition", method = RequestMethod.GET)
    public String getCurrentExhibition(HttpServletRequest request, ModelMap modelMap) {
        long startTime=System.currentTimeMillis();
        GbUtil gbUtil=new GbUtil();
        ExhibitionUtil exhibitionUtil=new ExhibitionUtil();
        List<OtherZlBean> list=exhibitionUtil.ShowZLUtil(gbUtil.getConnection(),"",1);
        //List<JsonBean> exhibitionBeans = (List<JsonBean>) m.get("exhibitionBeans");
        String jsontext = getString(startTime, gbUtil, list);
        return jsontext;
    }


    //根据展厅即将展览
    @RequestLimit(count=6, time=600)
    @RequestMapping(value = "/upcomingExhibition", method = RequestMethod.GET)
    public String getUpcomingExhibition(HttpServletRequest request, ModelMap modelMap) {
        long startTime=System.currentTimeMillis();
        GbUtil gbUtil=new GbUtil();
        ExhibitionUtil exhibitionUtil=new ExhibitionUtil();
        System.out.println("开始执行");
        List<OtherZlBean> list=exhibitionUtil.ShowZLUtil(gbUtil.getConnection(),"",3);
        //List<JsonBean> exhibitionBeans = (List<JsonBean>) m.get("exhibitionBeans");
        String jsontext = getString(startTime, gbUtil, list);
        return jsontext;
    }

    //获取一段时间的展览活动
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/getZhanLanHDSomeTime", method = RequestMethod.GET)
    public String getZhanLanHDSomeTime(JsonBean jsonBean) {
        GbUtil gbUtil = new GbUtil();
        Map<String, Object> m = gbUtil.ShowSomeTimeZLHD(gbUtil.getConnection(),jsonBean.getStarttime(),jsonBean.getEndtime(),jsonBean);
        List<JsonBean> searchAllBeans = (List<JsonBean>) m.get("jsonBeans");

        Long sum = (Long) m.get("sum");
        String jsonsum="{\"sum\":\""+sum+"\"}]";
        String jsontext = gbUtil.toJson(searchAllBeans).toString();
        jsontext=jsontext.replaceAll("]",",");
        jsontext+=jsonsum;

        return jsontext;
    }

    public String getString(long startTime, GbUtil gbUtil, List<OtherZlBean> list) {
        String jsontext="";
        Map<String,String> map =new HashMap<>();
        map.put("msg","今日闭馆");
        if(list!=null){
            jsontext=gbUtil.toJson(list);
            System.out.println(jsontext);
            long endTime=System.currentTimeMillis();
            System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
        }else{
            jsontext= JSON.toJSONString(map);
        }
        return jsontext;
    }

    @RequestMapping("/searchCP")
    public Map searchZL(HttpServletRequest request){
        Map map = new HashMap();
        try {
            String pageSize =  request.getParameter("pageSize");
            String pageNum =  request.getParameter("pageNum");
            int pageSizeInt = 10000;
            int pageNumInt = 1;

            if(!StringUtils.isEmpty(pageSize)){
                pageSizeInt = Integer.parseInt(pageSize);
            }
            if(!StringUtils.isEmpty(pageNum)){
                pageNumInt = Integer.parseInt(pageNum);
            }


            GbUtil gbUtil = new GbUtil();
            TRSConnection con = gbUtil.getConnection();
            List cons = new ArrayList();
            cons.add(HybaseUtils.eqCond("chnls","75"));
            String sql = HybaseUtils.andCond(cons);
            map = gbUtil.searchAllByChannelid(pageNumInt,pageSizeInt,sql,"-id_int",con,0);


        }catch (Exception e){
            map.put("success","fail");
            map.put("message","数据参数异常");
            map.put("result","");
        }
        return map;
    }

    @RequestMapping("/searchZL")
    public Map searchCP(HttpServletRequest request){
        Map map = new HashMap();
        try {
            String pageSize =  request.getParameter("pageSize");
            String pageNum =  request.getParameter("pageNum");
            int pageSizeInt = 10000;
            int pageNumInt = 1;
            if(!StringUtils.isEmpty(pageSize)){
                pageSizeInt = Integer.parseInt(pageSize);
            }
            if(!StringUtils.isEmpty(pageNum)){
                pageNumInt = Integer.parseInt(pageNum);
            }


            GbUtil gbUtil = new GbUtil();
            TRSConnection con = gbUtil.getConnection();
            List cons = new ArrayList();
            cons.add(HybaseUtils.eqCond("chnls","80"));
            String sql = HybaseUtils.andCond(cons);
            map = gbUtil.searchAllByChannelid(pageNumInt,pageSizeInt,sql,"-id_int",con,0);


        }catch (Exception e){
            map.put("success","fail");
            map.put("message","数据参数异常");
            map.put("result","");
        }
        return map;
    }

}
