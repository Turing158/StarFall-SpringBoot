package com.starfall.service;

import com.starfall.dao.MessageDao;
import com.starfall.dao.UserDao;
import com.starfall.entity.Message;
import com.starfall.entity.MessageTerm;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageDao messageDao;
    @Autowired
    UserDao userDao;
    @Autowired
    WebSocket webSocket;

    public ResultMsg getMessageList(String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        List<Message> messages = messageDao.findAllMsgByToUser(user);
        List<MessageTerm> messageList = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            String[] content = messages.get(i).getContent().split("[&divide&]");
            String lastContent = content[content.length-1];
            if(messageList.isEmpty()){
                messageList = messageListAdd(messageList,messages,i,user,lastContent);
            }
            else{
                boolean flag = true;
                for (int j = 0; j < messageList.size(); j++) {
                    if(
                            messageList.get(j).getUser().equals(messages.get(i).getToUser())
                                    || messageList.get(j).getUser().equals(messages.get(i).getFromUser())
                    ){
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    messageList = messageListAdd(messageList,messages,i,user,lastContent);
                }
            }
        }
        return ResultMsg.success(messageList);
    }

    public List<MessageTerm> messageListAdd(List<MessageTerm> messageList, List<Message> messages, int i, String user, String lastContent) {
        MessageTerm messageTerm;
        if(messages.get(i).getFromUser().equals(user)){
            messageTerm = new MessageTerm(
                    messages.get(i).getToUser(),
                    messages.get(i).getToName(),
                    messages.get(i).getToAvatar(),
                    lastContent
            );
        }
        else {
            messageTerm = new MessageTerm(
                    messages.get(i).getFromUser(),
                    messages.get(i).getFromName(),
                    messages.get(i).getFromAvatar(),
                    lastContent
            );
        }
        messageList.add(messageTerm);
        return messageList;
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
            String date = now.getYear()+"-"+fillZero(now.getMonthValue()+1+"")+"-"+fillZero(now.getDayOfMonth()+"")+" "+fillZero(now.getHour()+"")+":"+fillZero(now.getMinute()+"")+":"+fillZero(now.getSecond()+"");
            Message message = new Message(fromUser,fromUserObj.getName(),fromUserObj.getAvatar(),toUser,toUserObj.getName(),toUserObj.getAvatar(),date,content);
            webSocket.sendMessageToUser(toUser, JsonOperate.toJson(message));
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
                    messageDao.updateMsgContent(fromUser,toUser,oldDateTimeStr,newContent);
                }
                else{
//                    直接保存新数据
                    messageDao.insertMsg(fromUser,toUser,date,content);
                }
            }
            return ResultMsg.success(message);
        }
        return ResultMsg.error("USER_ERROR");
    }


    public String fillZero(String str){
        if(str.length() == 1){
            return "0"+str;
        }
        return str;
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
