package com.starfall.controller;

import com.starfall.entity.Message;
import com.starfall.entity.ResultMsg;
import com.starfall.service.AdminMessageService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/chat")
public class AdminMessageController {
    @Autowired
    private AdminMessageService messageService;

    @PostMapping("/adminFindAllMessage")
    public ResultMsg findAllMessage(int page) {
        return messageService.findAllMessage(page);
    }

    @PostMapping("/adminInsertMessage")
    public ResultMsg insertMessage(@RequestBody Message message) {
        return messageService.insertMessage(message);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static
    class Messages{
        Message newMessage;
        Message oldMessage;
    }
    @PostMapping("/adminUpdateMessage")
    public ResultMsg updateMessage(@RequestBody Messages messages) {
        return messageService.updateMessage(messages.getNewMessage(),messages.getOldMessage());
    }

    @PostMapping("/adminDeleteMessage")
    public ResultMsg deleteMessage(@RequestBody Message message) {
        return messageService.deleteMessage(message);
    }
}
