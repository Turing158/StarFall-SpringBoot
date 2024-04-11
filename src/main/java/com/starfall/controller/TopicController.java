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
import org.springframework.web.bind.annotation.RequestHeader;
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
    public ResultMsg getLike(int id, @RequestHeader("Authorization")String token){
        return topicService.getLike(id,token);
    }

    @PostMapping("/like")
    public ResultMsg likeOrDisLike(int id,int like,@RequestHeader("Authorization")String token){
        return topicService.like(id,token,like);
    }



    @PostMapping("/findCommentByTopic")
    public ResultMsg findTopicLabel(int id,int page ){
        return topicService.findCommentByTopicId(id,page);
    }


    @PostMapping("/appendComment")
    public ResultMsg appendComment(HttpSession session, int id, @RequestHeader("Authorization")String token, String content,String code){
        return topicService.appendComment(session,id,token,content,code);
    }

    @PostMapping("/deleteComment")
    public ResultMsg deleteComment(int id,@RequestHeader("Authorization")String token,String date){
        return topicService.deleteComment(id, token, date);
    }

    @PostMapping("/appendTopic")
    public ResultMsg appendTopic(HttpSession session,@RequestHeader("Authorization")String token, @RequestBody TopicIn topicin){
        return topicService.appendTopic(session,token,topicin);

    }

    @PostMapping("/isPromiseToEdit")
    public ResultMsg isPromiseToEdit(@RequestHeader("Authorization") String token,int id){
        System.out.println(id);
        return topicService.isPromiseToEditTopic(token,id);
    }


    @PostMapping("/hasToPromiseToEdit")
    public ResultMsg hasToPromiseToEdit(@RequestHeader("Authorization") String token,int id){
        return topicService.findTopicInfoToEdit(token,id);
    }


    @PostMapping("/editTopic")
    public ResultMsg editTopic(HttpSession session,@RequestHeader("Authorization")String token, @RequestBody TopicIn topicin){
        return topicService.updateTopic(session,token,topicin);
    }


    @PostMapping("/deleteTopic")
    public ResultMsg deleteTopic(@RequestHeader("Authorization")String token,int id){
        return topicService.deleteTopic(token,id);
    }

}
