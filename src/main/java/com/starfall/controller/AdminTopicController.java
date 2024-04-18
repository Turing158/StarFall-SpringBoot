package com.starfall.controller;


import com.starfall.entity.*;
import com.starfall.service.AdminTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/topic")
public class AdminTopicController {
    @Autowired
    private AdminTopicService topicService;

    @PostMapping("/adminFindAllTopic")
    public ResultMsg findAllTopic(int page) {
        return topicService.findAllTopic(page);
    }

    @PostMapping("/adminAddTopic")
    public ResultMsg addTopic(@RequestBody TopicOut topicOut) {
        System.out.println(topicOut);
        return topicService.addTopic(topicOut);
    }

    @PostMapping("/adminUpdateTopic")
    public ResultMsg updateTopic(@RequestBody TopicOut topicOut) {
        return topicService.updateTopic(topicOut);
    }

    @PostMapping("/adminDeleteTopic")
    public ResultMsg deleteTopic(int id) {
        return topicService.deleteTopic(id);
    }

    @PostMapping("/adminFindAllTopicComment")
    public ResultMsg findAllTopicCommentById(int id,int page) {
        return topicService.findAllTopicComment(id,page);
    }
    @PostMapping("/adminFindAllTopicForSelect")
    public ResultMsg findAllTopicSelect() {
        return topicService.findAllTopicSelect();
    }

    @PostMapping("/adminAddTopicComment")
    public ResultMsg addTopicComment(@RequestBody Comment comment) {
        return topicService.addTopicComment(comment);
    }

    @PostMapping("/adminUpdateTopicComment")
    public ResultMsg updateTopicComment(@RequestBody Comment comment) {
        return topicService.updateTopicComment(comment);
    }

    @PostMapping("/adminDeleteTopicComment")
    public ResultMsg deleteTopicComment(@RequestBody Comment comment) {
        return topicService.deleteTopicComment(comment);
    }



}
