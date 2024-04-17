package com.starfall.controller;

import com.starfall.service.AdminNoticeService;
import com.starfall.entity.Notice;
import com.starfall.entity.ResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/notice")
public class AdminNoticeController {
    @Autowired
    private AdminNoticeService noticeService;
    @PostMapping("/adminFindAllNotice")
    public ResultMsg findAllNotice(int page) {
        return noticeService.findAllNotice(page);
    }

    @PostMapping("/adminAddNotice")
    public ResultMsg addNotice(@RequestBody Notice notice) {
        return noticeService.addNotice(notice);
    }


    @PostMapping("/adminUpdateNotice")
    public ResultMsg updateNotice(@RequestBody Notice notice) {
        return noticeService.updateNotice(notice);
    }

    @PostMapping("/adminDeleteNotice")
    public ResultMsg deleteNotice(int id) {
        return noticeService.deleteNotice(id);
    }
}
