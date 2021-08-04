package com.yc.service;

import com.yc.bean.Bussiness;
import com.yc.bean.PageBean;

import java.util.Map;

/**
 * @author CH
 */
public interface ResfoodBiz {
    PageBean<Bussiness> findResfoodByPage(int pagenum , int pagesize, String location );
}
