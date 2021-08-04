package com.yc.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class detail implements Serializable {
    private int cid;
    private String name;
    private  String fphoto;
private BigDecimal normprice;
        private BigDecimal   realprice;
 private    String discount;
         private Integer   count;

    @Override
    public String toString() {
        return "detail{" +
                "cid=" + cid +
                ", name='" + name + '\'' +
                ", fphoto='" + fphoto + '\'' +
                ", normprice=" + normprice +
                ", realprice=" + realprice +
                ", discount='" + discount + '\'' +
                ", count=" + count +
                '}';
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFphoto() {
        return fphoto;
    }

    public void setFphoto(String fphoto) {
        this.fphoto = fphoto;
    }

    public BigDecimal getNormprice() {
        return normprice;
    }

    public void setNormprice(BigDecimal normprice) {
        this.normprice = normprice;
    }

    public BigDecimal getRealprice() {
        return realprice;
    }

    public void setRealprice(BigDecimal realprice) {
        this.realprice = realprice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
