package com.yc.service;

import com.yc.bean.CartItem;
import com.yc.bean.Order;

import com.yc.dao.DBHelper;
import com.yc.web.servlet.PayServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.Map;

public class OrderServiceImp implements OrderService {
    @Override
    public void Insert(Order order, Map<String, CartItem> shopCart) throws SQLException {
        DBHelper db = new DBHelper();
        String roid=order.getRoid();
        Connection con=null;
        String sql1="insert into resorder(roid,userid,ordertime) values(?,?,?)";
        try{
            con=db.getConnection();
            //关闭事务提交，不允许一次提交一条sql语句到数据库执行
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(sql1);
            ps.setString(1,roid);
            ps.setString(2,String.valueOf(order.getUserid()));
            ps.setTimestamp(3,new Timestamp(order.getOrdertime().getTime()));

            ps.executeUpdate();


            if (shopCart!=null && shopCart.size()>0){
                for (Map.Entry<String, CartItem> entry:shopCart.entrySet()){
                    sql1="insert into resorderitem(roid,fid,dealprice,num,name) values(?,?,?,?,?)";
                    ps=con.prepareStatement(sql1);
                    ps.setString(1,roid);
                    ps.setString(2,String.valueOf(entry.getValue().getBussiness().getFid()));
                    ps.setString(3,String.valueOf(entry.getValue().getSmallCount()));
                    ps.setString(4,String.valueOf(entry.getValue().getNum()));
                    ps.setString(5,entry.getKey());
                    ps.executeUpdate();
                }
            }

            con.commit();

        }catch (Exception e){
            Log log = LogFactory.getLog(OrderServiceImp.class);
            log.info("事务回滚了........");
            if (con!=null){
                con.rollback();   //回滚
            }
            e.printStackTrace();
            throw e;
        }finally {
            if (con!=null){
                con.setAutoCommit(true);
                con.close();
            }
        }
    }


}

