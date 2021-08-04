package com.yc.bean;

import java.io.Serializable;
import java.util.List;

public class PageBean<T> implements Serializable {

    private List<T> data;
    private Integer pagenum;  //当前第几页
    private Integer totalpages;  //总页数
    private Integer totals;    //总记录数
    private Integer pagesize;  //每页多少条
    private Integer prepage;   //前一页的页号
    private Integer nextpage;  //后一页的页号
    private List<Long> scardList; //每个菜品的收藏数

    public List<Long> getScardList() {
        return scardList;
    }

    public void setScardList(List<Long> scardList) {
        this.scardList = scardList;
    }

    public void setTotalpages(Integer totalpages) {
        this.totalpages = totalpages;
    }

    public void setPrepage(Integer prepage) {
        this.prepage = prepage;
    }

    public void setNextpage(Integer nextpage) {
        this.nextpage = nextpage;
    }

    public Integer getNextpage(){
        getTotalpages();
        if (pagenum>=totalpages){
            nextpage=totalpages;
        }else {
            nextpage=pagenum+1;
        }
        return nextpage;
    }

    public Integer getPrepage() {
        if (pagenum<=1){
            prepage=1;
        }else {
            prepage=pagenum-1;
        }
        return prepage;
    }

    public Integer getTotalpages(){
        if (pagesize==0){
            pagesize=5;
        }
        if (totals%pagesize==0){
            totalpages=totals/pagesize;
        }else {
            totalpages=totals/pagesize+1;
        }
        return totalpages;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Integer getPagenum() {
        return pagenum;
    }

    public void setPagenum(Integer pagenum) {
        this.pagenum = pagenum;
    }

    public Integer getTotals() {
        return totals;
    }

    public void setTotals(Integer totals) {
        this.totals = totals;
    }

    public Integer getPagesize() {
        return pagesize;
    }

    public void setPagesize(Integer pagesize) {
        this.pagesize = pagesize;
    }
}
