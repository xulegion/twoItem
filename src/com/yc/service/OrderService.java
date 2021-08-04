package com.yc.service;

import com.yc.bean.CartItem;
import com.yc.bean.Order;


import java.sql.SQLException;
import java.util.Map;

public interface OrderService {
    //插入订单
    void Insert(Order resorder, Map<String, CartItem> shopCart) throws SQLException;
}
