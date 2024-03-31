package com.starfall.service;

import com.starfall.dao.TopicDao;
import com.starfall.entity.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TopicService {

    @Autowired
    TopicDao topicDao;

    public ResultMsg findAllTopic(int page,String label,String version){
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
        return ResultMsg.success(list,num);
    }


    public ResultMsg getTopicInfo(int id){
        boolean flag = topicDao.findTopicInfoById(id) != null;
        if(flag){
            return ResultMsg.success(topicDao.findTopicInfoById(id));
        }
        return ResultMsg.error("ID_ERROR");
    }


    public ResultMsg findAllTopicByUser(int page,String user){
        return ResultMsg.success(topicDao.findTopicByUser((page-1)*10,user),topicDao.findTopicTotalByUser(user));
    }


    public ResultMsg findTopicVersion(){
        return ResultMsg.success(topicDao.findTopicVersion());
    }

    public ResultMsg getLike(int topicId,String user) {
        LikeLog like = topicDao.findLikeByTopicAndUser(topicId,user);
        if(like != null && like.getStatus() == 1){
            return ResultMsg.warning("IS_LIKE",topicDao.findLikeTotalByTopic(topicId));
        }
        else if(like != null && like.getStatus() == 2){
            return ResultMsg.warning("IS_DISLIKE");
        }
        return ResultMsg.error("NOT_LIKE");
    }

    public ResultMsg like(int topicId,String user,int like){
        LocalDateTime LDT = LocalDateTime.now();
        String date = LDT.getYear() + "-" + LDT.getMonthValue() + "-" + LDT.getDayOfMonth() + " " + LDT.getHour() + ":" + LDT.getMinute() + ":" + LDT.getSecond();
        LikeLog likeObj = topicDao.findLikeByTopicAndUser(topicId,user);
        if(likeObj != null){
            if(likeObj.getStatus() != like){
                topicDao.updateLikeStateByTopicAndUser(topicId,user,like,date);
                if(like == 1){
                    return ResultMsg.warning("UPDATE_LIKE",topicDao.findLikeTotalByTopic(topicId));
                }
                return ResultMsg.warning("UPDATE_LIKE");
            }
            topicDao.updateLikeStateByTopicAndUser(topicId,user,0,date);
            return ResultMsg.warning("ALREADY_LIKE");
        }
        topicDao.insertLike(topicId,user,like,date);
        if(like == 1){
            return ResultMsg.warning("LIKE_SUCCESS",topicDao.findLikeTotalByTopic(topicId));
        }
        return ResultMsg.warning("LIKE_SUCCESS");
    }


    public ResultMsg findCommentByTopicId(int id,int page){
        return ResultMsg.success(topicDao.findCommentByTopicId(id,(page-1)*10),topicDao.findCommentCountByTopicId(id));
    }


    public ResultMsg appendComment(HttpSession session, int topicId, String user, String content, String code){
        String codeSession = (String) session.getAttribute("code");
        if(codeSession.equals(code)){
            LocalDateTime LDT = LocalDateTime.now();
            String date = LDT.getYear() + "-" + LDT.getMonthValue() + "-" + LDT.getDayOfMonth() + " " + LDT.getHour() + ":" + LDT.getMinute() + ":" + LDT.getSecond();
            topicDao.insertComment(topicId,user,date,content);
            return ResultMsg.success(topicDao.findCommentCountByTopicId(topicId));
        }
        return ResultMsg.error("CODE_ERROR");
    }


    public ResultMsg deleteComment(int id,String user,String date){
        topicDao.deleteComment(id,user,date);
        return ResultMsg.success();
    }


    public ResultMsg appendTopic(HttpSession session, TopicIn topicIn){
        String codeSession = (String) session.getAttribute("code");
        if(codeSession.equals(topicIn.getCode())){
            int id = topicDao.findAll().get(0).getId() + 1;
            LocalDateTime LDT = LocalDateTime.now();
            String date = LDT.getYear() + "-" + LDT.getMonthValue() + "-" + LDT.getDayOfMonth() + " " + LDT.getHour() + ":" + LDT.getMinute() + ":" + LDT.getSecond();
            topicDao.insertTopic(
                    id,
                    topicIn.getTitle(),
                    topicIn.getLabel(),
                    topicIn.getUser(),
                    date,
                    topicIn.getVersion()
            );
            topicDao.insertTopicItem(
                    id,
                    topicIn.getTopicTitle(),
                    topicIn.getEnTitle(),
                    topicIn.getSource(),
                    topicIn.getAuthor(),
                    topicIn.getLanguage(),
                    topicIn.getAddress(),
                    topicIn.getDownload(),
                    topicIn.getContent()
            );
            return ResultMsg.success();
        }
        return ResultMsg.error("CODE_ERROR");
    }


}
