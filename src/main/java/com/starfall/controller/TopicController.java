package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.service.NoticeService;
import com.starfall.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TopicController {

    @Autowired
    TopicService topicService;

    @PostMapping("/findAllTopic")
    public ResultMsg findAllTopic(int page,String label,String version){
        return topicService.findAllTopic(page,label,version);
    }


    @PostMapping("/getTopicInfo")
    public ResultMsg getTopicInfo(int id){
        return topicService.getTopicInfo(id);
    }


    @PostMapping("/findAllTopicByUser")
    public ResultMsg findAllTopicByUser(int page,String user){
        return topicService.findAllTopicByUser(page,user);
    }

    @PostMapping("/findTopicVersion")
    public ResultMsg findTopicVersion(){
        return topicService.findTopicVersion();
    }


    @PostMapping("/findCommentByTopic")
    public ResultMsg findTopicLabel(int id,int page ){
        return topicService.findCommentByTopicId(id,page);
    }

}
