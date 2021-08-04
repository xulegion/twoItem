package com.yc.bean;

import java.io.Serializable;

/*
    web访问结果
 */
public class JsonModel<T> implements Serializable {
    private Integer code;    //1  0
    private T obj;     //数据
    private String msg;  //出错信息


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
