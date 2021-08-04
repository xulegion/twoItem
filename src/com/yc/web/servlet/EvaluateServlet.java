package com.yc.web.servlet;

import com.yc.bean.Comment;
import com.yc.bean.JsonModel;
import com.yc.bean.Resuser;
import com.yc.dao.DBHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@WebServlet("/evaluate.action")
public class EvaluateServlet extends BaseServlet {
    DBHelper db=new DBHelper();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if ("seeall".equals(op)) {
                seeallOp(request, response);
            } else if("sendcomment".equals(op)){
                sendcommentOp(request,response);
            } else if("mycomment".equals(op)){
                mycommentOp(request,response);
            } else {
                show404Common(request, response);
            }
        }catch (Exception ex){
            JsonModel jm=new JsonModel();
            jm.setCode(0);
            jm.setMsg( ex.getMessage());
            super.writeJson(response,jm);
            ex.printStackTrace();
        }
    }

    //我的评价
    private void mycommentOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonModel jm=new JsonModel();
        HttpSession session = request.getSession();
        if(session.getAttribute("resuser")==null){
            jm.setCode(0);
            jm.setMsg("请先登录");
            super.writeJson(response,jm);
            return;
        }
        Resuser resuser= (Resuser) session.getAttribute("resuser");
        String username = resuser.getUsername();
        String sql="select c.commentid,c.comment,c.time,b.fname from comment c,bussiness b where c.fid=b.fid and username=?";
        List<Map<String, String>> list = db.doSelect(sql, username);
        if(list==null || list.size()<=0){
            jm.setCode(0);
            jm.setMsg("您还没有评价");
            super.writeJson(response,jm);
            return;
        }
        jm.setCode(1);
        jm.setObj(list);
        super.writeJson(response,jm);

    }


    private void sendcommentOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonModel jm=new JsonModel();
        HttpSession session = request.getSession();
        if(session.getAttribute("resuser")==null){
            jm.setCode(0);
            jm.setMsg("您还未登录");
            super.writeJson(response,jm);
            return;
        }
        Resuser resuser = (Resuser) session.getAttribute("resuser");
        String username = resuser.getUsername();
        Integer userid = resuser.getUserid();
        String fid = request.getParameter("fid");
        String mycomment = request.getParameter("mycomment");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//注意月份是MM
        Date date=new Date();
        String time = sdf.format(date);
        String sql="insert into comment(userid,username,comment,time,fid) VALUES(?,?,?,?,?)";
        int rs = db.doUpdate(sql, userid, username, mycomment, time, fid);
        if(rs<=0){
            jm.setCode(0);
            jm.setMsg("发送失败");
            super.writeJson(response,jm);
            return;
        }
        jm.setCode(1);
        jm.setObj("插入成功");
        super.writeJson(response,jm);

    }

    private void seeallOp(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonModel jm=new JsonModel();
        String fid = request.getParameter("fid");
        String sql="select * from comment where fid=? order by commentid desc";
        List<Comment> commentList = db.getForList(Comment.class, sql,fid);
        if( commentList==null || commentList.size()<=0){
            jm.setCode(0);
            jm.setMsg("暂无评价");
            super.writeJson(response,jm);
            return;
        }
        jm.setCode(1);
        jm.setObj( commentList );
        super.writeJson(response,jm);
    }

}

