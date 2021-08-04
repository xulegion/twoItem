package com.yc.bean;


import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {

    private String roid;//订单编号
    private Integer userid;//用户名
    private Date ordertime;//下单时间


    public String getRoid() {
        return roid;
    }

    public void setRoid(String roid) {
        this.roid = roid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }


    public Date getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(Date ordertime) {
        this.ordertime = ordertime;
    }

}
