package com.starfall.service;

import com.starfall.dao.TopicDao;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {

    @Autowired
    TopicDao topicDao;

    public ResultMsg findAllTopic(int page,String label,String version){
        ResultMsg resultMsg = new ResultMsg();
        resultMsg.setMsg("SUCCESS");
        List<Topic> list = null;
        int num = 0;
        if(label.equals("无") && version.equals("无")){
            list = topicDao.findAllTopic((page-1)*10);
            num = topicDao.findTopicTotal();
        }
        else if(label.equals("无")){
            list = topicDao.findAllTopicVersion((page-1)*10,version);
            num = topicDao.findTopicTotalByVersion();
        }
        else if(version.equals("无")){
            list = topicDao.findAllTopicLabel((page-1)*10,label);
            num = topicDao.findTopicTotalByLabel();
        }
        else {
            list = topicDao.findAllTopicLabelAndVersion((page-1)*10,label,version);
            num = topicDao.findTopicTotalByLabelAndVersion();
        }
        resultMsg.setObject(list);
        resultMsg.setNum(num);
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


    public ResultMsg findTopicVersion(){
        ResultMsg resultMsg = new ResultMsg();
        resultMsg.setMsg("SUCCESS");
        resultMsg.setObject(topicDao.findTopicVersion());
        return resultMsg;
    }


    public ResultMsg findCommentByTopicId(int id,int page){
        ResultMsg resultMsg = new ResultMsg();
        resultMsg.setMsg("SUCCESS");
        resultMsg.setObject(topicDao.findCommentByTopicId(id,(page-1)*10));
        resultMsg.setNum(topicDao.findCommentCountByTopicId(id));
        return resultMsg;
    }




}
