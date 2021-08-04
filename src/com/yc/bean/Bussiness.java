package com.yc.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class Bussiness implements Serializable {
private Integer fid;
    private String fname;
    private String detail;
    private String fphoto;
    private Integer cid;
    private BigDecimal price;
    public String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getFphoto() {
        return fphoto;
    }

    public void setFphoto(String fphoto) {
        this.fphoto = fphoto;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Bussiness{" +
                "fid=" + fid +
                ", fname='" + fname + '\'' +
                ", detail='" + detail + '\'' +
                ", fphoto='" + fphoto + '\'' +
                ", cid=" + cid +
                ", price=" + price +
                ", location='" + location + '\'' +
                '}';
    }
}
