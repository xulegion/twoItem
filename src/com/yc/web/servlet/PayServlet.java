package com.yc.web.servlet;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

import com.alipay.api.request.AlipayTradePagePayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.yc.bean.CartItem;

import com.yc.bean.Order;
import com.yc.bean.Resuser;
import com.yc.dao.DBHelper;
import com.yc.dao.MyProperties;
import com.yc.service.OrderService;
import com.yc.service.OrderServiceImp;
import com.yc.utils.AliPayConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet("/aliPay.action")
public class PayServlet extends BaseServlet {

    private DBHelper db=new DBHelper();
    private static HashMap sessionMap = new HashMap<String,Resuser>();
    MyProperties properties = MyProperties.getInstance();
    Jedis jedis = new Jedis(properties.getProperty("redis_host"), Integer.parseInt(properties.getProperty("redis_port")));
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String notify = req.getParameter("op");
        if ("OpaliPay".equals(op)){
            try {
                String aliPay = aliPayOp(req, resp);
                resp.setContentType("text/html;charset=utf-8");
                PrintWriter out = resp.getWriter();
                out.println(aliPay);
                out.flush();
                out.close();
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
        }

        if ("Opnotify".equals(notify)){
            try {
                notifyAsync(req,resp);
            } catch (AlipayApiException | ParseException | SQLException e) {
                e.printStackTrace();
            }
        }


    }

    public void notifyAsync(HttpServletRequest req,HttpServletResponse resp) throws IOException, AlipayApiException, ServletException, ParseException, SQLException {
        Log log = LogFactory.getLog(PayServlet.class);

        log.info("PayServlet-->notifyAsync-->"+"进来了");

        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = req.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name =  iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("utf-8"), "utf-8");
            params.put(name, valueStr);
        }
        System.out.println(params);//查看参数都有哪些

        String trade_status = req.getParameter("trade_status");

        log.info("PayServlet-->notifyAsync-->trade_status"+trade_status);


        log.error("PayServlet-->notifyAsync-->sessionMap的长度:"+sessionMap.size());



//        log.error("PayServlet-->notifyAsync-->session"+session);
//        log.error("PayServlet-->notifyAsync-->resuser"+resuser);
        // 付款金额
        String total_amount = req.getParameter("total_amount");
        if ("TRADE_SUCCESS".equals(trade_status)) {

//            log.info("PayServlet*************************sessionId"+session.getId());

            //订单号
            String out_trade_nos = requestParams.get("out_trade_no")[0];

            String userid = jedis.get("userid" + out_trade_nos);
            String userintegral = jedis.get("userintegral" + out_trade_nos);
            String sessionId = jedis.get("orderId" + out_trade_nos);


            //下定时间
            String order_time = requestParams.get("notify_time")[0];
            //userid

//            Integer userid = resuser.getUserid();
             Double integral= Double.parseDouble(userintegral) + Double.valueOf(total_amount) ;  //重新设置积分

            //为了图省事，直接在这更新积分了
            db.doUpdate("update resuser set integral=? where userid=?",integral,userid);

            //获取商品购物车
            String jedisCart = jedis.get(sessionId+"cart");
            Map<String, CartItem> cart = new Gson().fromJson(jedisCart, new TypeToken<HashMap<String,CartItem>>(){}.getType());



            //这里可以更新订单的状态等等。。
            Order order=new Order();//创建订单
            order.setRoid(out_trade_nos);
            order.setUserid(Integer.valueOf(userid));
            //创建日期
            //插入下单日期
            //使用插入订单服务
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//注意月份是MM
            Date date = sdf.parse(order_time);
            order.setOrdertime(date);
            OrderService imp=new OrderServiceImp();
            imp.Insert(order,cart);
            //删除已经购买过的购物车中的数据
//            session.removeAttribute("cart");

            jedis.del(sessionId+"cart");

        }else {

    }
    }


    public String aliPayOp(HttpServletRequest req, HttpServletResponse resp) throws AlipayApiException {
        String name = req.getParameter("title");
        System.out.println("*******************"+name);
        String orderId = UUID.randomUUID().toString();   //订单号
        String amount = req.getParameter("totalPrice");   //总价格

        HttpSession session = req.getSession();
        Resuser resuser = (Resuser) session.getAttribute("resuser");

        Log log = LogFactory.getLog(PayServlet.class);

        log.info("PayServlet-->aliPayOp-->session"+session);
        log.info("PayServlet-->aliPayOp-->resuser"+resuser);

        Double integral = resuser.getIntegral();    //拿到当前用户积分，超过100打0.8折，超过500打0.7折
        if (integral>500){
            amount = String.valueOf(Integer.parseInt(amount) * 0.7);
        }else if (integral>100){
            amount = String.valueOf(Integer.parseInt(amount) * 0.8);
        }

        jedis.set("userid"+orderId, String.valueOf(resuser.getUserid()));
        jedis.set("userintegral"+orderId, String.valueOf(resuser.getIntegral()));
        jedis.set("orderId"+orderId,session.getId());    //sessionId

        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AliPayConfig.gatewayUrl,
                AliPayConfig.app_id,
                AliPayConfig.merchant_private_key,
                "json",
                AliPayConfig.charset,
                AliPayConfig.alipay_public_key,
                AliPayConfig.sign_type);
        //        page
        AlipayTradePagePayRequest alipayPageRequest = new AlipayTradePagePayRequest();
        alipayPageRequest.setNotifyUrl(AliPayConfig.notify_url);
        alipayPageRequest.setReturnUrl(AliPayConfig.return_url);


        //拼接参数
        alipayPageRequest.setBizContent("{\"out_trade_no\":\"" + orderId + "\","
                + "\"total_amount\":\"" + amount + "\","
                + "\"subject\":\"" + name + "\","
                + "\"body\":\"" + name + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求
        return alipayClient.pageExecute(alipayPageRequest).getBody();
    }
}
