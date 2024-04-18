package com.starfall.service;

import com.starfall.dao.AdminTopicDao;
import com.starfall.entity.*;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminTopicService {
    @Autowired
    private AdminTopicDao topicDao;

    public ResultMsg findAllTopic(int page) {
        List<TopicOut> topics = topicDao.findAllTopic((page-1)*10);
        int count = topicDao.countTopic();
        return ResultMsg.success(topics, count);
    }


    public ResultMsg addTopic(TopicOut topicOut) {
        if(topicDao.existTopicById(topicOut.getId()) == 0){
            if(topicOut.getId() == 0){
                topicOut.setId(topicDao.findLastTopicId()+1);
            }
            Topic topic = new Topic(topicOut.getId(),topicOut.getTitle(),topicOut.getLabel(),topicOut.getUser(),null,null,topicOut.getDate(),topicOut.getView(),0,topicOut.getVersion());
            TopicItem topicItem = new TopicItem(topicOut.getId(),topicOut.getTopicTitle(),topicOut.getEnTitle(),topicOut.getSource(),topicOut.getAuthor(),topicOut.getLanguage(),topicOut.getAddress(),topicOut.getDownload(),topicOut.getContent());
            int status1 = topicDao.addTopic(topic);
            int status2 = topicDao.addTopicItem(topicItem);
            return status1+status2 == 2 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("EXIST_ERROR");
    }


    public ResultMsg updateTopic(TopicOut topicOut) {
        if(topicDao.existTopicById(topicOut.getOldId()) == 1){
            if(topicOut.getId() == 0){
                topicOut.setId(topicOut.getOldId());
            }

            Topic topic = new Topic(topicOut.getId(),topicOut.getTitle(),topicOut.getLabel(),topicOut.getUser(),null,null,topicOut.getDate(),topicOut.getView(),0,topicOut.getVersion());
            TopicItem topicItem = new TopicItem(topicOut.getId(),topicOut.getTopicTitle(),topicOut.getEnTitle(),topicOut.getSource(),topicOut.getAuthor(),topicOut.getLanguage(),topicOut.getAddress(),topicOut.getDownload(),topicOut.getContent());
            if(topicOut.getOldId() != topicOut.getId()){

                int status1 = topicDao.deleteTopicItem(topicOut.getOldId());
                int status2 = topicDao.updateTopic(topicOut.getOldId(),topic);
                int status3 = topicDao.addTopicItem(topicItem);
                return status1+status2+status3 == 3 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
            }
            int status1 = topicDao.updateTopic(topicOut.getOldId(),topic);
            int status2 = topicDao.updateTopicItem(topicOut.getOldId(),topicItem);
            return status1+status2 == 2 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("NOT_EXIST_ERROR");
    }

    public ResultMsg deleteTopic(int id) {
        if(topicDao.existTopicById(id) == 1){
            int status1 = topicDao.deleteTopicItem(id);
            int status2 = topicDao.deleteTopic(id);
            return status1+status2 == 2 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("NOT_EXIST_ERROR");
    }

    public ResultMsg findAllTopicComment(int id, int page){
        List<Comment> topics = topicDao.findTopicCommentById(id,(page-1)*10);
        int count = topicDao.countTopicCommentById(id);
        return ResultMsg.success(topics,count);
    }

    public ResultMsg findAllTopicSelect(){
        List<Topic> topics = topicDao.findAllTopicSelect();
        return ResultMsg.success(topics);
    }

    public ResultMsg addTopicComment(Comment comment){
        if(topicDao.existComment(comment.getTopicId(), comment.getUser(), comment.getDate()) == 0){
            int status = topicDao.addComment(comment);
            return status == 1 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("EXIST_ERROR");
    }

    public ResultMsg updateTopicComment(Comment comment){
        if(topicDao.existComment(comment.getOldTopicId(), comment.getOldUser(), comment.getOldDate()) == 1){
            if(topicDao.existComment(comment.getTopicId(), comment.getUser(), comment.getDate()) == 0){
                int status = topicDao.updateComment(comment);
                return status == 1 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
            }
            return ResultMsg.error("EXIST_ERROR");
        }
        return ResultMsg.error("NOT_EXIST_ERROR");
    }

    public ResultMsg deleteTopicComment(Comment comment){
        if(topicDao.existComment(comment.getTopicId(), comment.getUser(), comment.getDate()) == 1){
            int status = topicDao.deleteComment(comment);
            return status == 1 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("NOT_EXIST_ERROR");
    }




}
