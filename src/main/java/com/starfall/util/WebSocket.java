package com.starfall.util;

import com.starfall.config.WebSocketConfig;
import io.jsonwebtoken.Claims;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@ServerEndpoint("/message/{user}")
public class WebSocket {
    private Session session;
    private String user;
    private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<>();
    private static ConcurrentHashMap<String,Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("user") String token) {
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        Session sessionObj = sessionMap.get(user);
        if(sessionObj != null){
            log.info("用户{}已经在线",user);
            return;
        }
        this.session = session;
        this.user = user;
        webSocketSet.add(this);
        sessionMap.put(user,session);
        log.info("有新连接加入！当前在线人数为{}", webSocketSet.size());
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        sessionMap.remove(user);
        log.info("有连接关闭！当前在线人数为{}", webSocketSet.size());
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("收到客户端发来的消息：{}", message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误:{}",error.getMessage());
        error.printStackTrace();
    }

    public void sendMessageAll(String message) {
        for (WebSocket item : webSocketSet) {
            log.info("广播消息：{}", message);
            item.session.getAsyncRemote().sendText(message);
        }
    }

    public void sendMessageToUser(String user, String message) {
        Session session = sessionMap.get(user);
        if (session != null) {
            log.info("发送消息给{}：{}", user, message);
            session.getAsyncRemote().sendText(message);
        } else {
            log.error("用户{}不在线！", user);
        }
    }


    public static void sendMessageToAnyUser(String[] user,String message){
        for (String u : user) {
            Session session = sessionMap.get(u);
            if (session != null) {
                log.info("发送消息给{}：{}", u, message);
                session.getAsyncRemote().sendText(message);
            } else {
                log.error("用户{}不在线！", u);
            }
        }
    }

}
