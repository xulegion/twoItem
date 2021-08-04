package com.yc.web.servlet;

import com.google.gson.Gson;

import com.yc.bean.JsonModel;
import com.yc.utils.BeanUtils;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CH
 * 通用servlet类
 */
public abstract class BaseServlet extends HttpServlet {
    protected String op;  //有漏洞 高并发的情况下op值不可确认
//    service 在doGet前面操作

    protected <T> T getTFromRequest(Class<T> cls, HttpServletRequest request) throws Exception {
        Map<String, String[]> map = request.getParameterMap();
        Map<String,String> result=new HashMap<>();
        for( Map.Entry<String,String[]> entry:map.entrySet()){
            String key = entry.getKey();
            String[] value = entry.getValue();
            result.put(key, value[0]);
        }

        T t = BeanUtils.parseMapToObject(result, cls);
        return t;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        这些代码重复操作
        op = req.getParameter("op");
        if(op==null ||"".equalsIgnoreCase(op)){
            show404Common(req,resp);
            return;
        }
        super.service(req, resp);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        doPost(request,response);
    }

    protected void show404Common(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=utf-8");  //编码格式
        PrintWriter out = resp.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("</head>");
        out.println("<body>");
        out.println("404<br/><hr/>查无此操作");
        out.println("</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }
    protected void writeJson(HttpServletResponse response, JsonModel jm) throws IOException {
        Gson gson = new Gson();
        String jsonString=gson.toJson(jm);
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.println(jsonString);
        out.flush();
        out.close();
    }
}
