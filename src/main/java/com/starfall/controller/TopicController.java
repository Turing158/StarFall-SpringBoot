package com.starfall.controller;

import com.starfall.entity.CommentOut;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.TopicIn;
import com.starfall.service.TopicService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topic")
public class TopicController {

    @Autowired
    TopicService topicService;

    @PostMapping("/findAllTopic")
    public ResultMsg findAllTopic(int page, String label, String version, String belong) {
        return topicService.findAllTopic(page, label, version, belong);
    }


    @PostMapping("/getTopicInfo")
    public ResultMsg getTopicInfo(@RequestHeader(name = "Authorization", required = false) String token, String id) {
        return topicService.getTopicInfo(token, id);
    }


    @PostMapping("/findAllTopicByUser")
    public ResultMsg findAllTopicByUser(int page, String user, @RequestHeader(name = "Authorization", required = false) String token) {
        return topicService.findAllTopicByUser(page, user, token);
    }

    @PostMapping("/getLike")
    public ResultMsg getLike(String id, @RequestHeader("Authorization") String token) {
        return topicService.getLike(id, token);
    }

    @PostMapping("/like")
    public ResultMsg likeOrDisLike(String id, int like, @RequestHeader("Authorization") String token) {
        return topicService.like(id, token, like);
    }


    @PostMapping("/findCommentByTopic")
    public ResultMsg findCommentByTopic(String id, int page) {
        return topicService.findCommentByTopicId(id, page);
    }


    @PostMapping("/appendComment")
    public ResultMsg appendComment(String id, @RequestHeader("Authorization") String token, String content, String code) {
        return topicService.appendComment(id, token, content, code);
    }

    @PostMapping("/deleteComment")
    public ResultMsg deleteComment(String id, @RequestHeader("Authorization") String token, String date) {
        return topicService.deleteComment(id, token, date);
    }

    @PostMapping("/appendTopic")
    public ResultMsg appendTopic(@RequestHeader("Authorization") String token, @RequestBody TopicIn topicin) {
        return topicService.appendTopic(token, topicin);

    }

    @PostMapping("/isPromiseToEdit")
    public ResultMsg isPromiseToEdit(@RequestHeader("Authorization") String token, String id) {
        return topicService.isPromiseToEditTopic(token, id);
    }


    @PostMapping("/hasToPromiseToEdit")
    public ResultMsg hasToPromiseToEdit(@RequestHeader("Authorization") String token, String id) {
        return topicService.findTopicInfoToEdit(token, id);
    }


    @PostMapping("/editTopic")
    public ResultMsg editTopic(@RequestHeader("Authorization") String token, @RequestBody TopicIn topicin) {
        return topicService.updateTopic(token, topicin);
    }


    @PostMapping("/deleteTopic")
    public ResultMsg deleteTopic(@RequestHeader("Authorization") String token, String id) {
        return topicService.deleteTopic(token, id);
    }


    @PostMapping("/search")
    public ResultMsg search(String key, String classification, int page) {
        return topicService.searchTopic(key, classification, page);
    }

    @PostMapping("/adjustTopicDisplay")
    public ResultMsg adjustTopicDisplay(String id, String reason, int display, @RequestHeader("Authorization") String token) {
        return topicService.adjustTopicDisplay(id, reason, display, token);
    }

    @PostMapping("/setCollectionStatus")
    public ResultMsg setCollectionStatus(String id, @RequestHeader("Authorization") String token) {
        return topicService.setCollectionStatus(id, token);
    }

    @PostMapping("/findCollectStatus")
    public ResultMsg findCollectStatus(String id, @RequestHeader("Authorization") String token) {
        return topicService.findCollectStatus(id, token);
    }

    @PostMapping("/findAllCollection")
    public ResultMsg findAllCollection(int page, @RequestHeader("Authorization") String token) {
        return topicService.findAllCollection(page, token);
    }

    @PostMapping("/findFirstPublicTopic")
    public ResultMsg findFirstPublicTopic() {
        return topicService.findFirstPublicTopic();
    }

    @PostMapping("/findFirstRefreshTopic")
    public ResultMsg findFirstRefreshTopic() {
        return topicService.findFirstRefreshTopic();
    }

    @PostMapping("/findFirstCommentTopic")
    public ResultMsg findFirstCommentTopic() {
        return topicService.findFirstCommentTopic();
    }

    @PostMapping("/findTopicGallery")
    public ResultMsg findTopicGallery(String id) {
        return topicService.findTopicGallery(id);
    }

    @PostMapping("/uploadTopicGallery")
    public ResultMsg uploadTopicGallery(String id, String label, String imgBase64, @RequestHeader("Authorization") String token) {
        return topicService.uploadTopicGallery(id, label, imgBase64, token);
    }

    @PostMapping("/deleteTopicGallery")
    public ResultMsg deleteTopicGallery(String id, String topicId, @RequestHeader("Authorization") String token) {
        return topicService.deleteTopicGallery(id, topicId, token);
    }

    @PostMapping("/topTopicComment")
    public ResultMsg topTopicComment(@RequestBody CommentOut comment, @RequestHeader("Authorization") String token) {
        return topicService.topTopicComment(comment, token);
    }

    @PostMapping("/findTopicFiles")
    public ResultMsg findTopicFiles(String topicId) {
        return topicService.findTopicFiles(topicId);
    }

    @PostMapping("/uploadTopicFile")
    public ResultMsg uploadTopicFile(String id, String fileName, String fileLabel, String fileBase64, @RequestHeader("Authorization") String token) {
        return topicService.uploadTopicFile(id, fileName, fileLabel, fileBase64, token);
    }

    @PostMapping("/deleteTopicFile")
    public ResultMsg deleteTopicFile(String id, String topicId, @RequestHeader("Authorization") String token) {
        return topicService.deleteTopicFile(id, topicId, token);
    }
}
