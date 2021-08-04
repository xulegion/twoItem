package com.yc.service;

import com.yc.bean.Bussiness;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface Serach {
    //传进来请求的session以及要查询的值
    List<Bussiness>  getSerachResult(HttpSession session, String inputValue) throws Exception;

}
