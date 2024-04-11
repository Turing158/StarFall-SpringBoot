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
        User fromUserObj= userDao.findByUserOrEmail(fromUser);
        User toUserObj= userDao.findByUserOrEmail(fromUser);
        if(fromUserObj != null){
            LocalDateTime now = LocalDateTime.now();
            String date = now.getYear()+"-"+(now.getMonthValue()+1)+"-"+now.getDayOfMonth()+" "+now.getHour()+":"+now.getMinute()+":"+now.getSecond();
            webSocket.sendMessageToUser(toUser, JsonOperate.toJson(new Message(fromUser,fromUserObj.getName(),fromUserObj.getAvatar(),toUser,toUserObj.getName(),toUserObj.getAvatar(),date,"Hello,admin")));
            List<Message> fromUserMsgs = messageDao.findFromUserMsgByFromUserAndToUser(fromUser,toUser);
            if(fromUserMsgs.isEmpty()){
//                直接保存新数据
                messageDao.insertMsg(fromUser,toUser,date,content);
            }else{
                Message fromUserMsg = fromUserMsgs.get(0);
                String oldDateTimeStr = fromUserMsg.getDate();
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime oldDateTime = LocalDateTime.parse(oldDateTimeStr,df);
                LocalDateTime newDateTime = LocalDateTime.parse(date,df);
                if(oldDateTime.plusMinutes(1).isAfter(newDateTime)){
//                    更新
                    String newContent = fromUserMsg.getContent()+"[&divide&]"+content;
                    messageDao.updateMsgContent(fromUser,toUser,newContent,oldDateTimeStr);
                }
                else{
//                    直接保存新数据
                    messageDao.insertMsg(fromUser,toUser,date,content);
                }
            }
            return ResultMsg.success();
        }
        return ResultMsg.error("USER_ERROR");
    }

    public ResultMsg testSend(){
        LocalDateTime now = LocalDateTime.now();
        String fromUser = "test11";
        String toUser = "admin";
        String date = now.getYear()+"-"+(now.getMonthValue()+1)+"-"+now.getDayOfMonth()+" "+now.getHour()+":"+now.getMinute()+":"+now.getSecond();
        webSocket.sendMessageToUser(toUser, JsonOperate.toJson(new Message(fromUser,null,null,toUser,null,null,date,"Hello,admin")));
        return ResultMsg.success();
    }
}
