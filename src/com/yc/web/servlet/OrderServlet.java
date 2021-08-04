package com.yc.web.servlet;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yc.bean.*;
import com.yc.dao.DBHelper;
import com.yc.dao.MyProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/order.action")
public class OrderServlet extends BaseServlet {
    DBHelper db =new DBHelper();
    MyProperties properties = MyProperties.getInstance();
    Jedis jedis = new Jedis(properties.getProperty("redis_host"), Integer.parseInt(properties.getProperty("redis_port")));
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if ("order".equals(op)) {
                orderOp(response, request);
            } else if ("updatenum".equals(op)) {
                updatenumOp(response,request);
            } else if("myorder".equals(op)){
                myorderOp(request,response);
            } else if("details".equals(op)){
                detailsOp(request,response);
            }
            else if ("clearAll".equals(op)) {
                clearAll(response, request);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void detailsOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonModel jm = new JsonModel();
        String roid = request.getParameter("roid");
        String sql="select * from resorderitem where roid=?";
        List<Resorderitem> resorderitemList = db.getForList(Resorderitem.class, sql, roid);
        if(resorderitemList==null||resorderitemList.size()<=0){
            jm.setCode(0);
            jm.setMsg("无法查看详情");
            super.writeJson(response,jm);
            return ;
        }
        Integer fid = resorderitemList.get(0).getFid();

        String sql1="select * from bussiness where fid=?";
        List<Bussiness> bussinessList = db.getForList(Bussiness.class, sql1, fid);
        if(bussinessList==null || bussinessList.size()<=0){
            jm.setCode(0);
            jm.setMsg("出错了，查询不到数据");
            return;
        }
        Bussiness bussiness = bussinessList.get(0);
        for (Resorderitem resorderitem: resorderitemList) {
            resorderitem.setName(bussiness.getFname()+"--->"+resorderitem.getName());
        }
        jm.setCode(1);
        jm.setObj( resorderitemList );
        super.writeJson(response,jm);

    }

    //查看我的订单
    private void myorderOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        JsonModel jm=new JsonModel();
        if( session.getAttribute("resuser")==null){
            jm.setCode(0);
            jm.setMsg("您还未登录");
            super.writeJson(response,jm);
            return;
        }
        Resuser resuser=(Resuser) session.getAttribute("resuser");
        Integer userid = resuser.getUserid();
        String sql="select * from resorder where userid=? ORDER BY ordertime desc";
        DBHelper db =new DBHelper();
        List<Order> orders = db.getForList(Order.class, sql, userid);

        if( orders==null || orders.size()<=0){
            jm.setCode(0);
            jm.setMsg("你还没有订单");
            super.writeJson(response,jm);
            return;
        }
        jm.setCode(1);
        jm.setObj( orders );
        super.writeJson(response,jm);
    }

    //更新数量
    private void updatenumOp(HttpServletResponse response, HttpServletRequest request) throws IOException {
        request.setCharacterEncoding("utf-8");
        HttpSession session = request.getSession();

        Log log = LogFactory.getLog(OrderServlet.class);
        log.error("OrderServlet--->updatenumOp*************************"+session.getId());

        System.out.println("OrderServlet--->updatenumOp*************************"+session.getId());

        long creationTime = session.getCreationTime();
        log.info("-------------"+creationTime);
        System.out.println("-------------"+creationTime);
        DBHelper db = new DBHelper();
        JsonModel jm = new JsonModel();
        //获取到新数据后
        int num=Integer.parseInt(request.getParameter("newnum"));
        String name=request.getParameter("name");
        List<detail> list=db.getForList(detail.class,"select * from combo where name=?",name);

        detail detail= list.get(0);

        Map<String, CartItem> cart;

        //获取原先不为空的购物车

        String jedisCart = jedis.get(session.getId()+"cart");
        cart = new Gson().fromJson(jedisCart, new TypeToken<HashMap<String,CartItem>>(){}.getType());
//        cart = (Map<String, CartItem>) session.getAttribute("cart");
        log.info("OrderServlet**************updatenumOp"+cart);
        System.out.println("OrderServlet**************updatenumOp"+cart);
        CartItem ci = null;
        if (cart.containsKey(name)) {

            ci = cart.get(name);
            int newnum = num;
            ci.setNum(newnum);
        } else {
            ci = new CartItem();
            //通过fid获取到这个对象
            ci.setDetail(detail);
            ci.setNum(num);
        }
        if (ci.getNum() <= 0) {
            cart.remove(name);
        } else {
            ci.getSmallCount();
            cart.put(name, ci);
        }

//        session.setAttribute("cart", cart);
        Gson gson = new Gson();
        String cartToJson = gson.toJson(cart);
        jedis.set(session.getId()+"cart",cartToJson);


        jm.setCode(1);
        jm.setObj(cart.values());
        super.writeJson(response, jm);
    }


    private void clearAll(HttpServletResponse response, HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();
        JsonModel jm = new JsonModel();
        jedis.del(session.getId()+"cart");
        jm.setCode(1);
        super.writeJson(response, jm);
    }

    private void orderOp(HttpServletResponse response, HttpServletRequest request) throws IOException, IllegalAccessException, InvocationTargetException, InstantiationException {
        request.setCharacterEncoding("utf-8");
        DBHelper db = new DBHelper();
        Gson gson = new Gson();
        JsonModel jm = new JsonModel();

        HttpSession session = request.getSession();

        Log log = LogFactory.getLog(OrderServlet.class);

        log.info("OrderServlet--->orderOp*************************"+session.getId());

        System.out.println("OrderServlet--->orderOp*************************"+session.getId());
            if (session.getAttribute("resuser") == null) {
            jm.setCode(-1);
            super.writeJson(response, jm);
            return;
        }

        int num=0;
        if (request.getParameter("num")!=null) {
           num = Integer.parseInt(request.getParameter("num"));
        }
        int fid = Integer.parseInt(request.getParameter("fid"));
        List<Bussiness> bussinesses = db.getForList(Bussiness.class, "select * from bussiness where fid=?", fid);
        String name=request.getParameter("name");

        List<detail> list=db.getForList(detail.class,"select * from combo where name=?",name);

        if (list == null || list.size() <= 0) {
            jm.setCode(0);
            jm.setMsg("查无此商品");
            super.writeJson(response, jm);
            return;
        }
        detail detail= list.get(0);
        //获取cid
       int cid=detail.getCid();
//        System.out.println(bussiness);        //购物车
        Map<String, CartItem> cart = null;


        if (jedis.get(session.getId()+"cart")!=null){
            String jedisCart = jedis.get(session.getId()+"cart");
            cart = new Gson().fromJson(jedisCart, new TypeToken<HashMap<String,CartItem>>(){}.getType());
        } else {
            //如果没有则创建新的购物车
            cart = new HashMap<String, CartItem>();
        }
        CartItem ci = null;
        if (cart.containsKey(name)) {
            ci = cart.get(name);
            int newnum = ci.getNum() + num;
            ci.setNum(newnum);
        } else {
            ci = new CartItem();
            //通过fid获取到这个对象
            ci.setDetail(detail);
            ci.setNum(num);
            ci.setBussiness(bussinesses.get(0));
        }
        if (ci.getNum() <= 0) {
            cart.remove(name);
        } else {
            ci.getSmallCount();
            cart.put(name, ci);
        }
        String cartToJson = gson.toJson(cart);
        jedis.set(session.getId()+"cart",cartToJson);
        jm.setCode(1);
        jm.setObj(cart.values());
        super.writeJson(response, jm);
    }
}

