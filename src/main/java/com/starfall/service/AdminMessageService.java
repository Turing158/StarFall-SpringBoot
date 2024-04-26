package com.starfall.service;

import com.starfall.dao.AdminMessageDao;
import com.starfall.entity.Message;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.SignIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminMessageService {
    @Autowired
    private AdminMessageDao messageDao;

    public ResultMsg findAllMessage(int page) {
        return ResultMsg.success(messageDao.findAllMessage((page-1)*10),messageDao.countMessage());
    }

    public ResultMsg insertMessage(Message message) {
        if(messageDao.existMessage(message.getFromUser(),message.getToUser(),message.getDate())==0) {
            int result = messageDao.insertMessage(message);
            return result == 1 ? ResultMsg.success() : ResultMsg.error("DATASOURCE_ERROR");
        }
        return ResultMsg.error("MESSAGE_EXIST");
    }

    public ResultMsg updateMessage(Message newMessage, Message oldMessage) {
        if(messageDao.existMessage(oldMessage.getFromUser(),oldMessage.getToUser(),oldMessage.getDate())==1){
            if(oldMessage.getFromUser().equals(newMessage.getFromUser())&&oldMessage.getToUser().equals(newMessage.getToUser())&&oldMessage.getDate().equals(newMessage.getDate())){
                int result = messageDao.updateMessage(newMessage);
                return result == 1 ? ResultMsg.success() : ResultMsg.error("DATASOURCE_ERROR");
            }
            int result = messageDao.updateMessageByOldMessage(newMessage,oldMessage);
            return result == 1 ? ResultMsg.success() : ResultMsg.error("DATASOURCE_ERROR");
        }
        return ResultMsg.error("MESSAGE_NOT_EXIST");
    }

    public ResultMsg deleteMessage(Message message) {
        if(messageDao.existMessage(message.getFromUser(),message.getToUser(),message.getDate())==1){
            int result = messageDao.deleteMessage(message.getFromUser(),message.getToUser(),message.getDate());
            return result == 1 ? ResultMsg.success() : ResultMsg.error("DATASOURCE_ERROR");
        }
        return ResultMsg.error("MESSAGE_NOT_EXIST");
    }
}
