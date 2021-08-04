package com.yc.web.servlet;

import com.yc.bean.Bussiness;
import com.yc.bean.Resuser;
import com.yc.dao.DBHelper;
import com.yc.service.BussinessInsert;
import com.yc.service.BussinessInsertImp;
import com.zhenzi.sms.ZhenziSmsClient;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

@WebServlet("/reg.action")
public class FileUploadServlet extends HttpServlet {


    /**
     * 店家名字：owner
     * 简介：brief
     * 手写图片：文件image
     * 原价：original
     * 现价：price
     * 套餐名：
     * 套餐详情：meal
     * 套餐图片：mealimage
     * 商家电话：phone
     * 省份：province
     * 城市：city
     * 地区：district
     * @param request
     * @param response
     * @throws IOException
     */

    public void doPost(HttpServletRequest request, HttpServletResponse response){

        try {
            registerOp(request,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerOp(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final long MAX_SIZE = 5 * 1024 * 1024;	//上传文件最大不超过5MB
        //设置编码格式支持中文显示
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
      //存放所有参数
        Map<String,String> result=new HashMap<>();

        //实例化一个硬盘工厂，用于创建ServletFileUpload对象
        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
        diskFileItemFactory.setSizeThreshold(4096);	//设置文件临时存放的内存大小为4KB,多余部分将临时存放在硬盘


        //用上述硬盘工厂实例化一个文件上传对象
        ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
        servletFileUpload.setSizeMax(MAX_SIZE);	//设置上传文件最大值

        PrintWriter out = response.getWriter();
        List fileList = new ArrayList();

        try {
            fileList = servletFileUpload.parseRequest(request);
            //解决上传文件为空的错误
            if(fileList == null || fileList.size() == 0) {
                showMessage("未选择文件,请选择!",request, response,0);
                return;
            }
        }catch(FileUploadException e) {
            //捕捉超过最大值的异常并显示错误提示
            if(e instanceof SizeLimitExceededException) {
                showMessage("文件过大,无法上传!",request, response,0);
                return;
            }
            showMessage("上传失败,请检查网络状况或联系相关技术人员!",request, response,0);
            e.printStackTrace();
        }

        Iterator fileIter = fileList.iterator(); //得到文件List的迭代器,以下进行保存文件操作
        int i=0;
        while(fileIter.hasNext()) {
            FileItem fileItem;
            fileItem = (FileItem) fileIter.next();
            //忽略不是文件域的表单字段
            if(fileItem == null || fileItem.isFormField()) {
                if(fileItem==null){
                    continue;
                }
                fileItem.getString("UTF-8");
                String name = fileItem.getFieldName();
                String value = fileItem.getString("utf-8");
                result.put(name,value);
                continue;
            }
            if(fileItem.getSize() != 0) {
                try {
                    byte[] data = null;
                    byte[] buf = new byte[1024];
                    InputStream is = fileItem.getInputStream();
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    int numBytesRead = 0;
                    while ((numBytesRead = is.read(buf)) != -1) {
                        output.write(buf, 0, numBytesRead);
                    }
                    data = output.toByteArray();
                    output.close();
                    is.close();
                    /**
                     * fastdfs
                     */
                    String conf_filename = "fdfs_client.conf";   //配置文件

                    //初始化参数
                    ClientGlobal.init(conf_filename);

                    String group_name;
                    String remote_filename;
                    ServerInfo[] servers;
                    TrackerClient tracker = new TrackerClient();
                    TrackerServer trackerServer = tracker.getTrackerServer();     //获取tracker服务器

                    StorageServer storageServer = null;    //存储服务器

                    StorageClient client = new StorageClient(trackerServer, storageServer);

                    NameValuePair[] meta_list;
                    String[] results;

                    int errno;

                    meta_list = new NameValuePair[4];
                    meta_list[0] = new NameValuePair("width", "800");
                    meta_list[1] = new NameValuePair("heigth", "600");
                    meta_list[2] = new NameValuePair("bgcolor", "#FFFFFF");
                    meta_list[3] = new NameValuePair("author", "xxn");

                    group_name = null;
                    StorageServer[] storageServers = tracker.getStoreStorages(trackerServer, group_name);
                    if (storageServers == null) {
                        System.err.println("获取存储服务器失败，代码: " + tracker.getErrorCode());
                    } else {
                        System.err.println("存储服务器数量: " + storageServers.length + "\n它们的信息如下:");
                        for (int k = 0; k < storageServers.length; k++) {
                            System.err.println((k + 1) + ". " + storageServers[k].getInetSocketAddress().getAddress().getHostAddress() + ":" + storageServers[k].getInetSocketAddress().getPort());
                        }
                        System.err.println("");
                    }

                    //上传一个普通的文本文件，返回值 为  String[]
                    results = client.upload_file(data, "jpg", meta_list);

                    if (results == null) {
                        System.err.println("上传文件失败，错误码: " + client.getErrorCode());
                        return;
                    } else {
                        String url=null;    //图片线上路径
                        group_name = results[0];   //组编号
                        remote_filename = results[1];   //文件名
                        System.err.println("组名: " + group_name + ", 文件名: " + remote_filename);
                        System.err.println(client.get_file_info(group_name, remote_filename));

                        url="http://39.103.237.140/"+group_name+"/"+remote_filename;


                        result.put(String.valueOf(i),url);
                        i++;

                        servers = tracker.getFetchStorages(trackerServer, group_name, remote_filename);
                        if (servers == null) {
                            System.err.println("get storage servers fail, error code: " + tracker.getErrorCode());
                        } else {
                            System.err.println("storage servers count: " + servers.length);
                            for (int k = 0; k < servers.length; k++) {
                                System.err.println((k + 1) + ". " + servers[k].getIpAddr() + ":" + servers[k].getPort());
                            }
                            System.err.println("");
                        }
                        //设置上传的元信息
                        meta_list = new NameValuePair[4];
                        meta_list[0] = new NameValuePair("width", "1024");
                        meta_list[1] = new NameValuePair("heigth", "768");
                        meta_list[2] = new NameValuePair("bgcolor", "#000000");
                        meta_list[3] = new NameValuePair("title", "Untitle");


                        errno = client.set_metadata(group_name, remote_filename, meta_list, ProtoCommon.STORAGE_SET_METADATA_FLAG_MERGE);

                        if (errno == 0) {
                            System.err.println("set_metadata success");
                        } else {
                            System.err.println("set_metadata fail, error no: " + errno);
                        }

                        meta_list = client.get_metadata(group_name, remote_filename);
                        System.out.println("设置好的元信息如下:");
                        if (meta_list != null) {
                            for (int j = 0; j < meta_list.length; j++) {
                                System.out.println(meta_list[j].getName() + " " + meta_list[j].getValue());
                            }
                        }
                    }
                }catch(Exception e) {
                    showMessage("上传失败,请检查网络状况或联系相关技术人员!",request, response,0);
                    e.printStackTrace();
                }
            }else {
                showMessage("文件内容为空,请检查!",request, response,0);
                return;
            }
            showMessage("新产品成功上架!",request, response,1);
        }
        tipCust("https://sms_developer.zhenzikj.com","109635","9a0fba56-8544-4c07-9cf1-3db3d96bed06");
        BussinessInsert bussinessInsert=new BussinessInsertImp();
        bussinessInsert.isnertBussiness(result);



    }

    private void tipCust(String apiUrl,String appId,String appSecret) throws Exception {
        DBHelper db = new DBHelper();
        List<Resuser> resusers = db.getForList(Resuser.class, "select * from resuser");

        ZhenziSmsClient client = new ZhenziSmsClient(apiUrl, appId, appSecret);
        Map<String, Object> params = new HashMap<String, Object>();

        String[] templateParams = new String[1];    //模板参数
        params.put("templateId", "6192");
        for (Resuser user: resusers) {
            if ("root".equals(user.getUsername())){
                continue;
            }
            params.put("number",user.getPhone());
            templateParams[0] = user.getUsername();
            params.put("templateParams", templateParams);
            String result = client.send(params);
            System.out.println("*********"+result);
        }
    }


    //展示信息
    public void showMessage(String message,HttpServletRequest request, HttpServletResponse response,Integer status) {
        response.setContentType("text/html;charset=utf-8");
        try {
            PrintWriter out = response.getWriter();
            out.print("<script type='text/javascript'>alert('" + message + "');</script>");
           if(status==1){
               out.print("<script type='text/javascript'>window.open('index.html','_self');</script>");
           }else{
               out.print("<script type='text/javascript'>window.open('reg.html?isshow=true','_self');</script>");
           }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}