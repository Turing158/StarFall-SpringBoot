package com.starfall.service;

import com.starfall.dao.AdminTopicDao;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.Topic;
import com.starfall.entity.TopicItem;
import com.starfall.entity.TopicOut;
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


    public ResultMsg addTopic(Topic topic, TopicItem topicItem) {
        if(topicDao.existTopicById(topic.getId()) == 0){
            if(topic.getId() == 0){
                topic.setId(topicDao.countTopic()+1);
            }
            int status1 = topicDao.addTopic(topic);
            int status2 = topicDao.addTopicItem(topic.getId(),topicItem);
            return status1+status2 == 2 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("EXIST_ERROR");
    }


    public ResultMsg updateTopic(Topic topic,TopicItem topicItem) {
        if(topicDao.existTopicById(topic.getId()) == 1){
            if(topic.getId() == 0){
                topic.setId(topicDao.countTopic()+1);
            }
            int status1 = topicDao.updateTopic(topic);
            int status2 = topicDao.updateTopicItem(topic.getId(),topicItem);
            return status1+status2 == 2 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("NOT_EXIST_ERROR");
    }

    public ResultMsg deleteTopic(int id) {
        if(topicDao.existTopicById(id) == 1){
            int result = topicDao.deleteTopic(id);
            if (result == 1) {
                return ResultMsg.success();
            }
            return ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("NOT_EXIST_ERROR");
    }
}
