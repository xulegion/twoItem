package com.yc.service;

import com.yc.bean.Bussiness;
import com.yc.bean.CartItem;
import com.yc.dao.DBHelper;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class BussinessInsertImp implements  BussinessInsert {
    @Override
    public void isnertBussiness(Map<String, String> map) throws SQLException {
        DBHelper db=new DBHelper();
        Connection con=null;
        //传递进来存放所有值的map

        //取出商家名
        String owner=map.get("owner");
        //取出地区
        String district=map.get("district");
       String city=map.get("city");
        //存入数据库当中的时候标准fname写法
        String fname="【"+district+"】"+owner;
        String brief=map.get("brief");//获取商家简介
        String price=map.get("price");
        //进行商家插入
        String sql1="insert into bussiness(fname,detail,fphoto,price,location) values(?,?,?,?,?)";
        int cid=0;
        //进行事务操作
        try{
            con=db.getConnection();
            //同时能获取主键值
            PreparedStatement pstmt = con.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            //关闭事务提交，不允许一次提交一条sql语句到数据库执行
            con.setAutoCommit(false);
            pstmt.setString(1,fname);
            pstmt.setString(2,brief);
            pstmt.setString(3,map.get("0"));
            pstmt.setString(4,price);
            pstmt.setString(5,city);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                 cid = rs.getInt(1);//主键的数据类型为int
            }

            String sql2="update bussiness set cid=? where fname=?";
            pstmt=con.prepareStatement(sql2);
            pstmt.setString(1,String.valueOf(cid));
            pstmt.setString(2,fname);
            pstmt.executeUpdate();



            //套餐表进行插入
            String mealname=map.get("meal");
            String mealImg=map.get("1");
            String original=map.get("original");//获取到原价
            String sql3="insert into combo(cid, name,fphoto,normprice,realprice,discount,count) values(?,?,?,?,?,5,1)";
            pstmt=con.prepareStatement(sql3);
            pstmt.setString(1, String.valueOf(cid));
            pstmt.setString(2,mealname);
            pstmt.setString(3,mealImg);
            pstmt.setString(4,original);
            pstmt.setString(5,price);
            pstmt.executeUpdate();


            con.commit();
        }catch (Exception e){
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

