package com.yc.bean;

import java.io.Serializable;

public class CartItem implements Serializable {
    private static final long SerialVsersionUID = 6;
    private double smallCount;

    private Bussiness bussiness;
    private int num;
    private detail detail;

    public detail getDetail() {
        return detail;
    }

    public void setDetail(detail detail) {
        this.detail = detail;
    }

    //获取小计
    public double getSmallCount() {
        this.smallCount = num * (detail.getRealprice().intValue());
        return smallCount;
    }

    public void setSmallCount(double smallCount) {
        this.smallCount = smallCount;
    }

    public Bussiness getBussiness() {
        return bussiness;
    }

    public void setBussiness(Bussiness food) {
        this.bussiness = food;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
