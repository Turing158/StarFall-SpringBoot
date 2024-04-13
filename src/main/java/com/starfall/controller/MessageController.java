package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
    @Autowired
    MessageService messageService;


    @PostMapping("/findMessageList")
    public ResultMsg findMessageList(@RequestHeader("Authorization") String token){
        return messageService.getMessageList(token);
    }

    @PostMapping("/findMsgByToUserAndFromUser")
    public ResultMsg findMsgByToUserAndFromUser(@RequestHeader("Authorization") String token,String fromUser){
        return messageService.getMsgByToUserAndFromUser(token,fromUser);
    }


    @PostMapping("/sendMessage")
    public ResultMsg sendMessage(@RequestHeader("Authorization") String token,String toUser,String content){
        return messageService.SendMessage(token,toUser,content);
    }


    @GetMapping("/testSend")
    public ResultMsg testSend(){
        return messageService.testSend();
    }
}
