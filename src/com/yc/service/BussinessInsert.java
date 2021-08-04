package com.yc.service;

import java.sql.SQLException;
import java.util.Map;

public interface BussinessInsert {
    void isnertBussiness(Map<String, String> map) throws SQLException;
}
