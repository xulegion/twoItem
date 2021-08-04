package com.yc.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author CH
 */
public class Test {
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        String time = "2019-09-19";
        Date date = ft.parse(time);
        System.out.println(date);
    }
}
