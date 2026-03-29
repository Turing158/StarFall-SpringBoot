package com.starfall.service;

import com.starfall.Exception.ServiceException;
import com.starfall.util.EncDecUtil;
import com.starfall.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@ServerEndpoint("/message/{user}")
public class WebSocketService {
    private Session session;
    private String user;
    private static CopyOnWriteArraySet<WebSocketService> webSocketServiceSet = new CopyOnWriteArraySet<>();
    private static ConcurrentHashMap<String,Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("user") String token) {
        log.info("【websocket连接】token:{}",token);
        Claims claims;
        if(token == null || token.trim().isEmpty()){
            return;
        }
        try{
            log.info("【websocket连接】token:{}",token);
            claims = JwtUtil.parseTokenStatic(token);
        }
        catch (Exception e){
            log.info("【websocket连接】错误的token:{}",token);
            onError(session, e);
            return;
        }
        String tokenUser = (String) claims.get("USER");
        Session sessionObj = sessionMap.get(tokenUser);
        if(sessionObj != null){
            return;
        }
        this.session = session;
        this.user = tokenUser;
        webSocketServiceSet.add(this);
        sessionMap.put(tokenUser,session);
        log.info("【websocket连接】用户{}连接加入！当前在线人数为{}", tokenUser,webSocketServiceSet.size());
    }

    @OnClose
    public void onClose() {
        webSocketServiceSet.remove(this);
        if(user != null && !user.trim().isEmpty()){
            sessionMap.remove(user);
        }
        log.info("【websocket连接】用户{}连接关闭！当前在线人数为{}", user,webSocketServiceSet.size());
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("【websocket连接】收到客户端发来的消息：{}", message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("【websocket连接】发生错误:{}",error.getMessage());
        error.printStackTrace();
    }

    public void sendMessageAll(String message) {
        for (WebSocketService item : webSocketServiceSet) {
            log.info("【websocket连接】广播消息：{}", message);
            item.session.getAsyncRemote().sendText(message);
        }
    }

    public void sendMessageToUser(String user, String message) {
        Session session = sessionMap.get(user);
        if (session != null) {
            log.info("【websocket连接】发送消息给{}：{}", user, message);
            session.getAsyncRemote().sendText(message);
        }
    }


    public static void sendMessageToAnyUser(String[] user,String message){
        log.info("【websocket连接】发送消息给[{}]：{}", String.join(",",user), message);
        for (String u : user) {
            Session session = sessionMap.get(u);
            if (session != null) {
                session.getAsyncRemote().sendText(message);
            }
        }
    }

}
