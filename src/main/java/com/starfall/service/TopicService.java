package com.starfall.service;

import com.starfall.dao.TopicDao;
import com.starfall.entity.ResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicService {

    @Autowired
    TopicDao topicDao;

    public ResultMsg findAllTopic(){
        ResultMsg resultMsg = new ResultMsg();
        resultMsg.setMsg("SUCCESS");
        resultMsg.setData(topicDao.findAllTopic());
        return resultMsg;
    }
}
