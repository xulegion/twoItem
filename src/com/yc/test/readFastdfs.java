package com.yc.test;


import org.csource.common.IniFileReader;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.IOException;


public class readFastdfs {


    @Test
    public void test1() throws IOException {
        String conf_filename = "fdfs_client.conf";
        IniFileReader iniFileReader = new IniFileReader(conf_filename);
        System.out.println("getConfFilename: " + iniFileReader.getConfFilename());
        System.out.println("connect_timeout: " + iniFileReader.getIntValue("connect_timeout", 3));
        System.out.println("network_timeout: " + iniFileReader.getIntValue("network_timeout", 45));
        System.out.println("charset: " + iniFileReader.getStrValue("charset"));
        System.out.println("http.tracker_http_port: " + iniFileReader.getIntValue("http.tracker_http_port", 8080));
        System.out.println("http.anti_steal_token: " + iniFileReader.getBoolValue("http.anti_steal_token", false));
        System.out.println("http.secret_key: " + iniFileReader.getStrValue("http.secret_key"));
        String[] tracker_servers = iniFileReader.getValues("tracker_server");
        if (tracker_servers != null) {
            System.out.println("tracker_servers.length: " + tracker_servers.length);
            for (int i = 0; i < tracker_servers.length; i++) {
                System.out.println(String.format("tracker_servers[%s]: %s", i, tracker_servers[i]));
            }

        }
    }


    @Test
    public void test2() throws IOException, MyException {

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
        byte[] file_buff;
        NameValuePair[] meta_list;
        String[] results;

        int errno;

        meta_list = new NameValuePair[4];
        meta_list[0] = new NameValuePair("width", "800");
        meta_list[1] = new NameValuePair("heigth", "600");
        meta_list[2] = new NameValuePair("bgcolor", "#FFFFFF");
        meta_list[3] = new NameValuePair("author", "xxn");

        file_buff = "this is a test".getBytes(ClientGlobal.g_charset);


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
        results = client.upload_file(file_buff, "txt", meta_list);


  		/*
  		group_name = "";
  		results = client.upload_file(group_name, file_buff, "txt", meta_list);
  		*/
        if (results == null) {
            System.err.println("上传文件失败，错误码: " + client.getErrorCode());
            return;
        } else {
            group_name = results[0];   //组编号
            remote_filename = results[1];   //文件名
            System.err.println("组名: " + group_name + ", 文件名: " + remote_filename);
            System.err.println(client.get_file_info(group_name, remote_filename));

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
                for (int i = 0; i < meta_list.length; i++) {
                    System.out.println(meta_list[i].getName() + " " + meta_list[i].getValue());
                }
            }

            file_buff = client.download_file(group_name, remote_filename);


            if (file_buff != null) {
                System.out.println("文件长度:" + file_buff.length);
                System.out.println((new String(file_buff)));
            }


        }
    }
}
