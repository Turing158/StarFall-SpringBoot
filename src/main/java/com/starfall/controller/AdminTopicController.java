package com.starfall.controller;


import com.starfall.entity.ResultMsg;
import com.starfall.entity.Topic;
import com.starfall.entity.TopicItem;
import com.starfall.service.AdminTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResultMsg addTopic(Topic topic, TopicItem topicItem) {
        return topicService.addTopic(topic,topicItem);
    }


}
