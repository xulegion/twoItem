package com.yc.web.servlet;


import com.yc.bean.JsonModel;
import com.yc.bean.Bussiness;
import com.yc.bean.PageBean;
import com.yc.bean.detail;
import com.yc.dao.DBHelper;
import com.yc.dao.MyProperties;
import com.yc.service.ResfoodBizImpl;
import com.yc.service.Serach;
import com.yc.service.SerachImp;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/shop.action")
public class ShopServlet extends BaseServlet {

    private ResfoodBizImpl rbi=new ResfoodBizImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(op.equals("Opshop")){
            Opshop( request,  response);
        }else if(op.equals("shopDetail")){
            shopDetailOp(request,response);
        }else if(op.equals("search")){
            try {
                searchOp(request,response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if("getcity".equals(op)){
            getAllCity(request,response);
        }else{
            show404Common(request, response);
        }
    }

    //获取城市信息
    private void getAllCity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DBHelper db=new DBHelper();
        JsonModel jm=new JsonModel();
        String sql="select distinct location from bussiness";
        List<Map<String, String>> list=db.doSelect(sql);
        if(list==null || list.size()<=0){
            jm.setCode(0);
            jm.setMsg("出错");
            super.writeJson(response,jm);
            return;
        }
        jm.setCode(1);
        jm.setObj(list);
        super.writeJson(response,jm);
    }

    //模糊查询
    private void searchOp(HttpServletRequest request, HttpServletResponse response) throws Exception {

        List list =new ArrayList();
        DBHelper db=new DBHelper();
        JsonModel jm = new JsonModel();
        Serach serach=new SerachImp();
        String name= request.getParameter("name");
        HttpSession session=request.getSession();
        List<Bussiness> bussinesses = serach.getSerachResult(session, name);

        if(bussinesses!=null&&bussinesses.size()>0){
            jm.setCode(1);
        }
        if (bussinesses==null || bussinesses.size()<=0){
            bussinesses = db.getForList(Bussiness.class, "select * from bussiness");
            jm.setCode(0);
            jm.setMsg("无信息");
        }

        MyProperties properties = MyProperties.getInstance();
        Jedis jedis = new Jedis(properties.getProperty("redis_host"), Integer.parseInt(properties.getProperty("redis_port")));
        List<Long> scardList=new ArrayList();
        for (Bussiness bussiness : bussinesses) {
            Integer fid = bussiness.getFid();
            Long scard = jedis.scard(fid + "");
            scardList.add( scard );
        }

        list.add( bussinesses );
        list.add( scardList );

        jm.setObj(list );
        super.writeJson(response,jm);
    }

    //获取商品的详细信息
    private void shopDetailOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DBHelper db=new DBHelper();
        JsonModel jm=new JsonModel();
        //获取到fid后，查询数据库的详细信息
        int fid=Integer.parseInt(request.getParameter("fid"));
        List<Object> resulst=new ArrayList<>();
        List<Bussiness> list = db.getForList(Bussiness.class,"select * from bussiness where fid=?", fid);
        Bussiness bus=list.get(0);
        //将获取到的cid转换为数字类型在进行查询
        int cid=bus.getCid();
        String sql2="select * from combo where cid=?";
        //解决名字详情问题3
        List<detail> list1=db.getForList(detail.class,sql2,cid);
        jm.setCode(1);
        resulst.add(list);
        resulst.add(list1);

        jm.setObj(resulst);
        super.writeJson(response,jm);

    }

    //获取页面信息
    private void Opshop(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        JsonModel jm=new JsonModel();
        DBHelper db=new DBHelper();
        HttpSession session = req.getSession();

        int pagenum=1;
        if(req.getParameter("pagenum")!=null){
            pagenum=Integer.parseInt(  req.getParameter("pagenum"));
        }
        int pagesize=4;
        if(req.getParameter("pagesize")!=null){
            pagesize=Integer.parseInt(  req.getParameter("pagesize"));
        }
        String location = req.getParameter("location");

        PageBean pb = rbi.findResfoodByPage(pagenum, pagesize,location);


            if(session.getAttribute("resuser")==null){
                jm.setCode(2);
            }else{
                jm.setCode(1);
            }
            jm.setObj( pb);
            super.writeJson( resp,jm);

//        JsonModel jm=new JsonModel();
//        DBHelper db=new DBHelper();
//        HttpSession session = req.getSession();
//        String location = req.getParameter("location");
//
//        String sql="select b.fid ,b.location,b.fname,b.detail,b.fphoto from bussiness as b\n" +
//                "left join\n" +
//                "(select fid,count(fid) num from comment group by fid) as a on a.fid=b.fid where b.location=? order by a.num  desc";
//        List<Map<String, String>> list= db.doSelect(sql,location);
//        if(list!=null&&list.size()>0){
//            if(session.getAttribute("resuser")==null){
//                jm.setCode(2);
//            }else{
//                jm.setCode(1);
//            }
//            jm.setObj(list);
//
//        }else{
//            jm.setCode(0);
//            jm.setMsg("无信息");
//        }
//
//        super.writeJson(resp,jm);


    }



}
