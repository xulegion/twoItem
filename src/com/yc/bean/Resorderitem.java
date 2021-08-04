package com.yc.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class Resorderitem implements Serializable {
    private Integer roiid;
    private String roid;
    private Integer fid;
    private BigDecimal dealprice;  //价格
    private Integer num;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRoiid() {
        return roiid;
    }

    public void setRoiid(Integer roiid) {
        this.roiid = roiid;
    }

    public String getRoid() {
        return roid;
    }

    public void setRoid(String roid) {
        this.roid = roid;
    }

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public BigDecimal getDealprice() {
        return dealprice;
    }

    public void setDealprice(BigDecimal dealprice) {
        this.dealprice = dealprice;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "Resorderitem{" +
                "roiid=" + roiid +
                ", roid='" + roid + '\'' +
                ", fid=" + fid +
                ", dealprice=" + dealprice +
                ", num=" + num +
                '}';
    }
}

