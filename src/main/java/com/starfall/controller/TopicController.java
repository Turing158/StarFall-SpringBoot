package com.starfall.controller;

import com.starfall.Exception.ParamException;
import com.starfall.entity.CommentVO;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.TopicDTO;
import com.starfall.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topic")
public class TopicController {

    @Autowired
    TopicService topicService;

//    @PostMapping("/findAllTopic")
    @PostMapping("/item/find")
    public ResultMsg findAllTopic(int page, String label, String version, String belong) {
        var r = topicService.findAllTopic(page, label, version, belong);
        return ResultMsg.success(r.getFirst(),r.getSecond());
    }


//    @PostMapping("/getTopicInfo")
    @PostMapping("/info/find")
    public ResultMsg getTopicInfo(@RequestHeader(name = "Authorization", required = false) String token, String id) {
        return ResultMsg.success(topicService.getTopicInfo(token, id));
    }


//    @PostMapping("/findAllTopicByUser")
    @PostMapping("/user/find")
    public ResultMsg findAllTopicByUser(int page, String user, @RequestHeader(name = "Authorization", required = false) String token) {
        var r = topicService.findAllTopicByUser(page, user, token);
        return ResultMsg.success(r.getFirst(),r.getSecond());
    }

//    @PostMapping("/getLike")
    @PostMapping("/like/find")
    public ResultMsg getLike(String id, @RequestHeader("Authorization") String token) {
        return topicService.getLike(id, token);
    }

//    @PostMapping("/like")
    @PostMapping("/like/update")
    public ResultMsg likeOrDisLike(String id, int like, @RequestHeader("Authorization") String token) {
        return topicService.like(id, token, like);
    }


//    @PostMapping("/findCommentByTopic")
    @PostMapping("/comment/find")
    public ResultMsg findCommentByTopic(String id, int page) {
        return topicService.findCommentByTopicId(id, page);
    }


//    @PostMapping("/appendComment")
    @PostMapping("/comment/insert")
    public ResultMsg appendComment(String id, @RequestHeader("Authorization") String token, String content, String code) {
        return topicService.appendComment(id, token, content, code);
    }

//    @PostMapping("/deleteComment")
    @PostMapping("/comment/delete")
    public ResultMsg deleteComment(String id, @RequestHeader("Authorization") String token, String date) {
        return topicService.deleteComment(id, token, date);
    }

//    @PostMapping("/appendTopic")
    @PostMapping("/info/insert")
    public ResultMsg appendTopic(@RequestHeader("Authorization") String token, @RequestBody TopicDTO topicin) {
        return topicService.appendTopic(token, topicin);

    }

//    @PostMapping("/isPromiseToEdit")
    @PostMapping("/edit/promise")
    public ResultMsg isPromiseToEdit(@RequestHeader("Authorization") String token, String id) {
        return topicService.isPromiseToEditTopic(token, id);
    }


    @PostMapping("/edit/find")
    public ResultMsg hasToPromiseToEdit(@RequestHeader("Authorization") String token, String id) {
        return topicService.findTopicInfoToEdit(token, id);
    }


    @PostMapping("/info/update")
    public ResultMsg editTopic(@RequestHeader("Authorization") String token, @RequestBody TopicDTO topicin) {
        return topicService.updateTopic(token, topicin);
    }


//    @PostMapping("/deleteTopic")
    @PostMapping("/delete")
    public ResultMsg deleteTopic(@RequestHeader("Authorization") String token, String id) {
        return topicService.deleteTopic(token, id);
    }


    @PostMapping("/search")
    public ResultMsg search(String key, String classification, int page) {
        return topicService.searchTopic(key, classification, page);
    }

//    @PostMapping("/adjustTopicDisplay")
    @PostMapping("/adjust")
    public ResultMsg adjustTopicDisplay(String id, String reason, int display, @RequestHeader("Authorization") String token) {
        if(display != -1 && display != 1){
            throw new ParamException("PARAM_ERROR", "display参数错误，只能为-1或1");
        }
        topicService.adjustTopicDisplay(id, reason, display, token);
        return ResultMsg.success();
    }

//    @PostMapping("/adjustTopicDisplayAgain")
    @PostMapping("/adjust/again")
    public ResultMsg adjustTopicDisplayAgain(String id, String reason, int display, String noticeId, @RequestHeader("Authorization") String token) {
        if(display != -1 && display != 1){
            throw new ParamException("PARAM_ERROR", "display参数错误，只能为-1或1");
        }
        if(noticeId == null){
            throw new ParamException("PARAM_ERROR", "noticeId参数不能为空");
        }
        topicService.adjustTopicDisplay(id, reason, display, token, noticeId);
        return ResultMsg.success();
    }

    @PostMapping("/adjust/complete")
    public ResultMsg topicRectificationComplete(String noticeId,String topicId,@RequestHeader("Authorization") String token){

        topicService.topicRectificationComplete(noticeId, topicId, token);
        return ResultMsg.success();
    }

//    @PostMapping("/setCollectionStatus")
    @PostMapping("/collect")
    public ResultMsg setCollectionStatus(String id, @RequestHeader("Authorization") String token) {
        return topicService.setCollectionStatus(id, token);
    }

//    @PostMapping("/findCollectStatus")
    @PostMapping("/collect/status")
    public ResultMsg findCollectStatus(String id, @RequestHeader("Authorization") String token) {
        return topicService.findCollectStatus(id, token);
    }

//    @PostMapping("/collection/person")
    @PostMapping("/collection/user/find")
    public ResultMsg findAllUserCollection(int page, String user) {
        var topics = topicService.findOtherCollection(user, page);
        return ResultMsg.success(topics.getFirst(),topics.getSecond());
    }

//    @PostMapping("/collection/all")
    @PostMapping("/collection/find")
    public ResultMsg findAllCollection(int page, @RequestHeader("Authorization") String token) {
        var topics = topicService.findAllCollection(page, token);
        return ResultMsg.success(topics.getFirst(), topics.getSecond());
    }

//    @PostMapping("/findFirstPublicTopic")
    @PostMapping("/public/first/find")
    public ResultMsg findFirstPublicTopic() {
        return ResultMsg.success(topicService.findFirstPublicTopic());
    }

//    @PostMapping("/findFirstRefreshTopic")
    @PostMapping("/refresh/first/find")
    public ResultMsg findFirstRefreshTopic() {
        return ResultMsg.success(topicService.findFirstRefreshTopic());
    }

//    @PostMapping("/findFirstCommentTopic")
    @PostMapping("/comment/first/find")
    public ResultMsg findFirstCommentTopic() {
        return ResultMsg.success(topicService.findFirstCommentTopic());
    }

//    @PostMapping("/findTopicGallery")
    @PostMapping("/gallery/find")
    public ResultMsg findTopicGallery(String id) {
        return topicService.findTopicGallery(id);
    }

//    @PostMapping("/uploadTopicGallery")
    @PostMapping("/gallery/upload")
    public ResultMsg uploadTopicGallery(String id, String label, String imgBase64, @RequestHeader("Authorization") String token) {
        return topicService.uploadTopicGallery(id, label, imgBase64, token);
    }

//    @PostMapping("/deleteTopicGallery")
    @PostMapping("/gallery/delete")
    public ResultMsg deleteTopicGallery(String id, String topicId, @RequestHeader("Authorization") String token) {
        return topicService.deleteTopicGallery(id, topicId, token);
    }

//    @PostMapping("/topTopicComment")
    @PostMapping("/comment/top/update")
    public ResultMsg topTopicComment(@RequestBody CommentVO comment, @RequestHeader("Authorization") String token) {
        return topicService.topTopicComment(comment, token);
    }

    @PostMapping("/comment/top/find")
    public ResultMsg findTopComment(String id) {
        return ResultMsg.success(topicService.findTopComment(id));
    }


//    @PostMapping("/findTopicFiles")
    @PostMapping("/files/find")
    public ResultMsg findTopicFiles(String topicId) {
        return topicService.findTopicFiles(topicId);
    }

//    @PostMapping("/uploadTopicFile")
    @PostMapping("/files/upload")
    public ResultMsg uploadTopicFile(String id, String fileName, String fileLabel, String fileBase64, @RequestHeader("Authorization") String token) {
        return topicService.uploadTopicFile(id, fileName, fileLabel, fileBase64, token);
    }

//    @PostMapping("/deleteTopicFile")
    @PostMapping("/files/delete")
    public ResultMsg deleteTopicFile(String id, String topicId, @RequestHeader("Authorization") String token) {
        return topicService.deleteTopicFile(id, topicId, token);
    }
}
