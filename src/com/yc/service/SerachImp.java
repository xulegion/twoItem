package com.yc.service;

import com.yc.bean.Bussiness;
import com.yc.dao.BussinessDao;

import javax.servlet.http.HttpSession;
import java.util.List;

public class SerachImp implements Serach {
    /*
    传入的参数为搜索框内给入的值
     */
    @Override
    public List<Bussiness> getSerachResult(HttpSession session, String inputValue) throws Exception {

        BussinessDao dao=new BussinessDao();
        dao.createIndex(session);
        List<Bussiness> res=dao.searchIndex(inputValue);

        return res;

    }
}
