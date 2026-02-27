package com.starfall.controller;


import com.starfall.entity.*;
import com.starfall.service.AdminTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/starfall/console/topic")
public class AdminTopicController {
    @Autowired
    private AdminTopicService topicService;

    @PostMapping("/adminFindAllTopic")
    public ResultMsg findAllTopic(int page,String keyword) {
        return topicService.findAllTopic(page,keyword);
    }

    @PostMapping("/adminAddTopic")
    public ResultMsg addTopic(@RequestBody TopicOut topicOut) {
        return topicService.addTopic(topicOut);
    }

    @PostMapping("/adminUpdateTopic")
    public ResultMsg updateTopic(@RequestBody TopicOut topicOut) {
        return topicService.updateTopic(topicOut);
    }

    @PostMapping("/adminDeleteTopic")
    public ResultMsg deleteTopic(String id) {
        return topicService.deleteTopic(id);
    }

    @PostMapping("/adminFindAllTopicComment")
    public ResultMsg findAllTopicCommentById(String id,int page,String keyword) {
        return topicService.findAllTopicComment(id,page,keyword);
    }
    @PostMapping("/adminFindAllTopicForSelect")
    public ResultMsg findAllTopicSelect(String keyword) {
        return topicService.findAllTopicSelect(keyword);
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

    @PostMapping("/adminFindAllTopicLikeItem")
    public ResultMsg findAllLikeLog(int page,String keyword) {
        return topicService.fndAllTopicLikeItem(page,keyword);
    }

    @PostMapping("/adminFindLikeItem")
    public ResultMsg findAllLikeLog(String id,int page,String keyword) {
        return topicService.findTopicItemById(id,page,keyword);
    }

    @PostMapping("/adminAddLikeItem")
    public ResultMsg addLikeLog(@RequestBody LikeLog likeLog){
        return topicService.addLikeLog(likeLog);
    }

    @PostMapping("/adminUpdateLikeItem")
    public ResultMsg updateLikeLog(@RequestBody LikeLog likeLog){
        return topicService.updateLikeLog(likeLog);
    }

    @PostMapping("/adminFindAllUserCollection")
    public ResultMsg findAllUserCollection(String user,int page){
        return topicService.findAllUserCollection(user,page);
    }

    @PostMapping("/adminAddUserCollection")
    public ResultMsg addUserCollection(@RequestBody Collection collection){
        return topicService.addUserCollection(collection);
    }

    @PostMapping("/adminDeleteUserCollection")
    public ResultMsg deleteUserCollection(String id,String user){
        return topicService.deleteUserCollection(id,user);
    }

    @PostMapping("/adminFindAllTopicGallery")
    public ResultMsg findAllTopicGallery(String id) {
        return topicService.findAllTopicGallery(id);
    }

    @PostMapping("/adminAddTopicGallery")
    public ResultMsg addTopicGallery(String id,String label,String user,String imgBase64) {
        return topicService.addTopicGallery(id, label, user, imgBase64);
    }

    @PostMapping("/adminDeleteTopicGallery")
    public ResultMsg deleteTopicGallery(String id) {
        return topicService.deleteTopicGallery(id);
    }

    @PostMapping("/adminFindAllTopicFile")
    public ResultMsg findAllTopicFile(String id) {
        return topicService.findAllTopicFile(id);
    }

     @PostMapping("/adminAddTopicFile")
    public ResultMsg addTopicFile(String id, String fileName, String fileLabel, String fileBase64) {
        return topicService.addTopicFile(id, fileName, fileLabel, fileBase64);
    }

    @PostMapping("/adminDeleteTopicFile")
    public ResultMsg deleteTopicFile(String id) {
        return topicService.deleteTopicFile(id);
    }
}
