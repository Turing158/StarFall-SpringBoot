package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NoticeController {

    @Autowired
    NoticeService noticeService;

    @PostMapping("/findAllNotice")
    public ResultMsg findAllNotice(){
        return noticeService.findAllNotice();
    }
}
