package com.yc.web.servlet;


import com.yc.bean.Bussiness;
import com.yc.bean.JsonModel;
import com.yc.bean.PageBean;
import com.yc.bean.Resuser;
import com.yc.dao.DBHelper;
import com.yc.dao.MyProperties;
import com.yc.utils.Encrypt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CH
 */
@WebServlet("/user.action")
public class UserServlet extends BaseServlet {
    DBHelper db=new DBHelper();
    MyProperties properties = MyProperties.getInstance();
    Jedis jedis = new Jedis(properties.getProperty("redis_host"), Integer.parseInt(properties.getProperty("redis_port")));
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {

            if ("checkUname".equals(op)) { //检查用户名是否重复
                checkUnameOp(request, response);
            } else if ("login".equals(op)) { //登录
                loginOp(request, response);
            } else if ("logout".equals(op)) {//退出
                logoutOp(request, response);
            } else if ("reg".equals(op)) { //注册
                regOp(request, response);
            } else if ("checkLogin".equals(op)) { //检查是否已经登陆
                checkLogin(request, response);
            } else if ("reset".equals(op)){
                resetOp(request,response);
            } else if ("pullIntegral".equals(op)){   //拉取用户积分
                pullIntegralOp(request,response);
            }else if ("pullStore".equals(op)){      //拉去用户收藏
                pullStoreOp(request,response);
            }else if ("isStroe".equals(op)){
                isStroeOp(request,response);
            }else if ("myStroe".equals(op)){
                myStroeOp(request,response);
            }else {
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

    private void myStroeOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonModel jm = new JsonModel();
        HttpSession session = request.getSession();
        List<Long> scardList= new ArrayList<>();    //我的收藏集合
        Resuser resuser = (Resuser) session.getAttribute("resuser");
        if (resuser!=null){
            Set<String> myStroe = jedis.smembers(resuser.getUsername());
            if (myStroe==null||myStroe.size()<=0){
                jm.setCode(2);    //暂无收藏
                jm.setMsg("暂无收藏");
             }else {
                List<Bussiness> bussinesses = new ArrayList<>();
                for (String stroe : myStroe) {
                    List<Bussiness> bussinesses1 = db.getForList(Bussiness.class, "select * from bussiness where fid=?", stroe);
                    bussinesses.add(bussinesses1.get(0));
                    Long scard = jedis.scard(stroe);
                    scardList.add( scard );
                }

                List list =new ArrayList();
                list.add(bussinesses);
                list.add( scardList );

                jm.setObj(list);
                jm.setCode(1);
            }
        }else {
            jm.setCode(0);
            jm.setMsg("用户未登录");
        }
        super.writeJson(response,jm);
    }

    private void isStroeOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonModel jm = new JsonModel();
        HttpSession session = request.getSession();
        Resuser resuser = (Resuser) session.getAttribute("resuser");

        if (resuser!=null){

            String fid = request.getParameter("fid");
            //存储对应用户的菜品
            String username = resuser.getUsername();
            Boolean isStroe = jedis.sismember(username, fid);
            if (isStroe==true){
                jedis.srem(username,fid);
            }else {
                jedis.sadd(username,fid);
            }
            Set<String> smembers = jedis.smembers(username);

            //存储对应菜品的用户数
            Boolean isUsStroe = jedis.sismember(fid, username);
            if (isUsStroe==true){
                jedis.srem(fid, username);
            }else {
                jedis.sadd(fid, username);
            }
//            Set<String> sm= jedis.smembers(fid);
//            System.out.println( fid+"用户"+ sm );
            Long scard = jedis.scard(fid);  //收藏数

            List list=new ArrayList();
            list.add( smembers );
            list.add( scard );
//            System.out.println( "集合"+list );
//            System.out.println( "第一"+list.get(1));

            jm.setCode(1);
            jm.setObj(list);    //拿到所有收藏对象和单个fid的点赞数
        }else {
            jm.setCode(0);     //用户未登录，无法收藏
        }
        super.writeJson(response,jm);
    }

    private void pullStoreOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonModel jm = new JsonModel();
        HttpSession session = request.getSession();
        Resuser resuser = (Resuser) session.getAttribute("resuser");
        if (resuser==null) {
            jm.setCode(0);    //还未登录，无法查看收藏了哪些
        }else {
            String username = resuser.getUsername();
            Set<String> smembers = jedis.smembers(username);
            jm.setObj(smembers);
            jm.setCode(1);     //取出当前用户所收藏的
        }
        super.writeJson(response,jm);

    }

    private void pullIntegralOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonModel jm = new JsonModel();
        HttpSession session = request.getSession();
        Resuser resuser = (Resuser) session.getAttribute("resuser");
        if (resuser==null){
            jm.setCode(0);   //用户还未登录，查取不了积分
        }else {
            jm.setCode(1);
            jm.setObj(resuser);
        }
        super.writeJson(response,jm);
    }

    private void resetOp(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        JsonModel jm=new JsonModel();
        String username = request.getParameter("username");
        String logsafecode = request.getParameter("logsafecode");
        String telemail = request.getParameter("telemail");
        String respassword = request.getParameter("respassword");

        if(logsafecode=="" ){
            jm.setCode(0);
            jm.setMsg("验证码不能为空");
            super.writeJson(response,jm);
            return;
        }

        String validateCode= (String) session.getAttribute("validateCode");
        if( !logsafecode.equals(validateCode) ){
            jm.setCode(0);
            jm.setMsg("验证码不匹配，请重新输入");
            super.writeJson(response,jm);
            return;
        }

        String sql1="select * from resuser where username=?";
        List<Map<String, String>> list = db.doSelect(sql1, username);
        if(list==null && list.size()<=0){
            jm.setCode(0);
            jm.setMsg("用户不存在");
            super.writeJson(response,jm);
            return;
        }

        respassword= Encrypt.md5(respassword);
        String sql="update resuser set pwd=? where username=? and phone=? or email=?";
        int rs = db.doUpdate(sql, respassword,username, telemail, telemail);
        if(rs>0){
            jm.setCode(1);

        }else{
            jm.setCode(0);
            jm.setMsg("重置失败");
        }
        super.writeJson(response,jm);
    }

    //检查登录
    private void checkLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        JsonModel jm=new JsonModel();
//        System.out.println(resuser1);
        //System.out.println(session.getAttribute("resuser"));
        if(session.getAttribute("resuser")==null){ //获取seesion中是否存在已登录的用户名
            jm.setCode(0);
            jm.setMsg("用户暂未登录。。。");
        }else{
            Resuser resuser = (Resuser) session.getAttribute("resuser");
            jm.setCode(1);
            jm.setObj(resuser);
        }
        super.writeJson(response,jm);
    }

    //注册
    private void regOp(HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession();
        JsonModel jm=new JsonModel();
        Resuser user = super.getTFromRequest(Resuser.class, request);
        String email = user.getEmail();
        String password = user.getPwd();
        String username = user.getUsername();
        String regsafecode = request.getParameter("regsafecode");
        String reppassword = request.getParameter("reppassword");
        Long tel = Long.valueOf(request.getParameter("tel"));

        String reg="^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        Pattern compile = Pattern.compile(reg);
        Matcher matcher = compile.matcher(email);
        boolean falg = matcher.matches();

        if(!falg){
            jm.setCode(0);
            jm.setMsg("邮箱格式不正确，不能注册用户");
            super.writeJson(response,jm);
            return;
        }

        //注册信息需要全部填
        if( email=="" || password=="" || username=="" || tel==null){
            jm.setCode(0);
            jm.setMsg("请输入完整的注册信息");
            super.writeJson(response,jm);
            return;
        }

        //匹配两次密码
        if(!reppassword.equals(password)){
            jm.setCode(0);
            jm.setMsg("两次输入的密码不一致！！！");
            super.writeJson(response,jm);
            return;
        }
        //匹配验证码
        String validateCode= (String) session.getAttribute("validateCode");
        if( !regsafecode.equals(validateCode) ){
            jm.setCode(0);
            jm.setMsg("验证码不匹配，请重新输入");
            super.writeJson(response,jm);
            return;
        }

        password = Encrypt.md5(password);
        String sql="insert into resuser(username,pwd,email,phone) values(?,?,?,?)";
        int rs = db.doUpdate(sql, username, password,email,tel);
        if(rs>0){
            jm.setCode(1);
        }else{
            jm.setCode(0);
            jm.setMsg("注册失败");
        }
        super.writeJson(response,jm);
    }

    //退出
    private void logoutOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        session.removeAttribute("resuser");
        JsonModel json = new JsonModel();
        json.setCode(1);
        super.writeJson(response,json);
    }

    private void loginOp(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        JsonModel jm = new JsonModel();
        String loginname = request.getParameter("loginname");
        String password = request.getParameter("password");
        password= Encrypt.md5(password);
        String logsafecode = request.getParameter("logsafecode");
        HttpSession session = request.getSession();
        String validateCode = (String)session.getAttribute("validateCode");
        if(logsafecode=="" ){
            jm.setCode(0);
            jm.setMsg("验证码不能为空");
            super.writeJson(response,jm);
            return;
        }
        if( !validateCode.equals(logsafecode)){
            jm.setCode(0);
            jm.setMsg("验证码不一致");
            super.writeJson(response,jm);
            return;
        }
        String sql="select * from resuser where pwd=? and (username=? or email=? or phone=?)";
        List<Resuser> resusers = db.getForList(Resuser.class, sql, password, loginname, loginname, loginname);

        Log log = LogFactory.getLog(UserServlet.class);

//        List<Map<String, String>> list = db.doSelect(sql, password, loginname,loginname,loginname);
        if(resusers.size()>0 && resusers!=null){
            Resuser resuser = resusers.get(0);
            session.setAttribute("resuser",resuser);

            Resuser resuser1 = (Resuser) session.getAttribute("resuser");
            log.info("UserServlet*********"+resuser1);

            if("root".equals(resuser.getUsername())){
                jm.setCode(2);    //客服
            }else{
                jm.setCode(1);
            }
            jm.setObj(resuser);

        }else{
            jm.setCode(0);
            jm.setMsg("用户名或者密码错误");

        }
        super.writeJson(response,jm);

    }

    private void checkUnameOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String tel = request.getParameter("tel");
        String sql="select * from resuser where username=? or email=? or phone=?";
        List<Map<String, String>> list =db.doSelect(sql,username,email,tel);
        JsonModel jm = new JsonModel();
        if(list!=null && list.size()>0){
            jm.setCode(1);
        }else{
            jm.setCode(0);
        }
        super.writeJson(response,jm);

    }


}
