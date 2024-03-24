package com.starfall.service;

import com.starfall.dao.TopicDao;
import com.starfall.entity.ResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicService {

    @Autowired
    TopicDao topicDao;

    public ResultMsg findAllTopic(int page){
        ResultMsg resultMsg = new ResultMsg();
        resultMsg.setMsg("SUCCESS");
        resultMsg.setObject(topicDao.findAllTopic((page-1)*10));
        resultMsg.setNum(topicDao.findTopicTotal());
        return resultMsg;
    }


    public ResultMsg getTopicInfo(int id){
        ResultMsg resultMsg = new ResultMsg();
        boolean flag = topicDao.findTopicInfoById(id) != null;
        if(flag){
            resultMsg.setObject(topicDao.findTopicInfoById(id));
            resultMsg.setMsg("SUCCESS");
            return resultMsg;
        }
        resultMsg.setMsg("ID_ERROR");
        return resultMsg;
    }


    public ResultMsg findAllTopicByUser(int page,String user){
        ResultMsg resultMsg = new ResultMsg();
        resultMsg.setMsg("SUCCESS");
        resultMsg.setObject(topicDao.findTopicByUser((page-1)*10,user));
        resultMsg.setNum(topicDao.findTopicTotalByUser(user));
        return resultMsg;
    }
}
