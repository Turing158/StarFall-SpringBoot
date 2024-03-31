package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.entity.TopicIn;
import com.starfall.entity.TopicOut;
import com.starfall.service.NoticeService;
import com.starfall.service.TopicService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/getLike")
    public ResultMsg getLike(int id,String user){
        return topicService.getLike(id,user);
    }

    @PostMapping("/like")
    public ResultMsg likeOrDisLike(int id,String user,int like){
        return topicService.like(id,user,like);
    }



    @PostMapping("/findCommentByTopic")
    public ResultMsg findTopicLabel(int id,int page ){
        return topicService.findCommentByTopicId(id,page);
    }


    @PostMapping("/appendComment")
    public ResultMsg appendComment(HttpSession session, int id, String user, String content,String code){
        return topicService.appendComment(session,id,user,content,code);
    }

    @PostMapping("/deleteComment")
    public ResultMsg deleteComment(int id,String user,String date){
        return topicService.deleteComment(id, user, date);
    }

    @PostMapping("/appendTopic")
    public ResultMsg appendTopic(HttpSession session, @RequestBody TopicIn topicin){
        return topicService.appendTopic(session,topicin);

    }


}
