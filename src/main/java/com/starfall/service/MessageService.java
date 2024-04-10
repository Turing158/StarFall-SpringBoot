package com.starfall.service;

import com.starfall.dao.MessageDao;
import com.starfall.dao.UserDao;
import com.starfall.entity.Message;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.User;
import com.starfall.util.JsonOperate;
import com.starfall.util.JwtUtil;
import com.starfall.util.WebSocket;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageDao messageDao;
    @Autowired
    UserDao userDao;
    @Autowired
    WebSocket webSocket;

    public ResultMsg getAllMsgByUser(String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        List<Message> messages = messageDao.findAllMsgByToUser(user);
        return ResultMsg.success(messages);
    }


    public ResultMsg getMsgByToUserAndFromUser(String token,String fromUser){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        List<Message> messages = messageDao.findMsgByToUserAndFromUser(user,fromUser);
        return ResultMsg.success(messages);
    }

    public ResultMsg SendMessage(String token,String toUser,String content){
        Claims claims = JwtUtil.parseJWT(token);
        String fromUser = (String) claims.get("USER");
        User user = userDao.findByUserOrEmail(fromUser);
        if(user != null){
            LocalDateTime now = LocalDateTime.now();
            String date = now.getYear()+"-"+(now.getMonthValue()+1)+"-"+now.getDayOfMonth()+" "+now.getHour()+":"+now.getMinute()+":"+now.getSecond();
            webSocket.sendMessageToUser(toUser, JsonOperate.toJson(new Message(fromUser,user.getName(),user.getAvatar(),toUser,date,content)));

            return ResultMsg.success();
        }
        return ResultMsg.error("USER_ERROR");
    }

    public ResultMsg testSend(){
        LocalDateTime now = LocalDateTime.now();
        String fromUser = "StarFall";
        String toUser = "admin";
        String date = now.getYear()+"-"+(now.getMonthValue()+1)+"-"+now.getDayOfMonth()+" "+now.getHour()+":"+now.getMinute()+":"+now.getSecond();
        webSocket.sendMessageToUser(toUser, JsonOperate.toJson(new Message(fromUser,null,null,toUser,date,"Hello,admin")));
        return ResultMsg.success();
    }
}
