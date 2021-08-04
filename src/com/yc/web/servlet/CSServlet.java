package com.yc.web.servlet;

import com.yc.dao.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 人工客服
 */
@ServerEndpoint(value="/CSServlet/{userId}")
public class CSServlet {

    private Logger logger = LoggerFactory.getLogger(Test.class);

    private Session session;   //某个客户端
    private String userId;

    private boolean isConnection=false;   //是否还在连接

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<CSServlet> webSocketSet = new CopyOnWriteArraySet<CSServlet>();
    //连接时执行
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) throws IOException{

        this.userId = userId;

        this.session=session;

        //自己加的
        this.isConnection=true;
        System.out.println("*********************"+this);
        webSocketSet.add(this);
    }

    //关闭时执行
    @OnClose
    public void onClose(){
        logger.info("连接：{} 关闭",this.userId);
        webSocketSet.remove(this);
        this.isConnection=false;
    }

    //收到消息时执行
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println(session);
        for (CSServlet csServlet:webSocketSet) {
            csServlet.session.getBasicRemote().sendText(userId+":"+message); //回复用户
        }
    }

    //连接错误时执行
    @OnError
    public void onError(Session session, Throwable error){
        logger.info("用户id为：{}的连接发送错误",this.userId);
        error.printStackTrace();
    }
}
