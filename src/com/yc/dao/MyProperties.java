 package com.yc.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

 public class MyProperties extends Properties{
         // TODO Auto-generated method stub
         private static MyProperties instance;

         private MyProperties() {
             InputStream is=this.getClass().getClassLoader().getResourceAsStream("db.properties");
             try {
                 this.load(is);
             } catch (IOException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }finally {
                 if(is!=null) {
                     try {
                         is.close();
                     } catch (IOException e) {
                         // TODO Auto-generated catch block
                         e.printStackTrace();
                     }
                 }
             }

         }

         public static MyProperties getInstance() {
             if(instance==null) {
                 instance=new MyProperties();
             }
             return instance;
         }
 }
