package com.starfall.service;

import com.starfall.dao.TopicDao;
import com.starfall.dao.UserDao;
import com.starfall.entity.*;
import com.starfall.util.Exp;
import com.starfall.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class TopicService {

    @Autowired
    TopicDao topicDao;

    @Autowired
    UserDao userDao;

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


    public ResultMsg getTopicInfo(String token,int id){
        TopicOut topic = topicDao.findTopicInfoById(id);
        boolean flag = topic != null;
        if(flag){
            if(token != null){
                int oldView = topic.getView();
                topicDao.updateTopicView(oldView+1,id);
                topic.setView(oldView+1);
            }
            return ResultMsg.success(topic);
        }
        return ResultMsg.error("ID_ERROR");
    }


    public ResultMsg findAllTopicByUser(int page,String user){
        return ResultMsg.success(topicDao.findTopicByUser((page-1)*10,user),topicDao.findTopicTotalByUser(user));
    }


    public ResultMsg findTopicVersion(){
        return ResultMsg.success(topicDao.findTopicVersion());
    }

    public ResultMsg getLike(int topicId,String token) {
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        LikeLog like = topicDao.findLikeByTopicAndUser(topicId,user);
        if(like != null && like.getStatus() == 1){
            return ResultMsg.warning("IS_LIKE",topicDao.findLikeTotalByTopic(topicId));
        }
        else if(like != null && like.getStatus() == 2){
            return ResultMsg.warning("IS_DISLIKE");
        }
        return ResultMsg.error("NOT_LIKE");
    }

    public ResultMsg like(int topicId,String token,int like){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
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


    public ResultMsg appendComment(HttpSession session, int topicId, String token, String content, String code){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        String codeSession = (String) session.getAttribute("code");
        if(codeSession.equals(code)){
            LocalDateTime LDT = LocalDateTime.now();
            String date = LDT.getYear() + "-" + LDT.getMonthValue() + "-" + LDT.getDayOfMonth() + " " + LDT.getHour() + ":" + LDT.getMinute() + ":" + LDT.getSecond();
            topicDao.insertComment(topicId,user,date,content);
            int count = topicDao.findCommentCountByTopicId(topicId);
            topicDao.updateTopicComment(count,topicId);
            return ResultMsg.success(count);
        }
        return ResultMsg.error("CODE_ERROR");
    }


    public ResultMsg deleteComment(int id,String token,String date){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        int status1 = topicDao.deleteComment(id,user,date);
        int count = topicDao.findCommentCountByTopicId(id);
        int status2 = topicDao.updateTopicComment(count,id);
        return status1+status2 == 2 ? ResultMsg.success(count) : ResultMsg.error("DELETE_ERROR");
    }


    public ResultMsg appendTopic(HttpSession session,String token, TopicIn topicIn){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        int level = userObj.getLevel();
        if(level < 5){
            return ResultMsg.error("LEVEL_ERROR");
        }
        String codeSession = (String) session.getAttribute("code");
        if(codeSession.equals(topicIn.getCode())){
            int id = topicDao.findAll().get(0).getId() + 1;
            LocalDateTime LDT = LocalDateTime.now();
            String date = LDT.getYear() + "-" + LDT.getMonthValue() + "-" + LDT.getDayOfMonth() + " " + LDT.getHour() + ":" + LDT.getMinute() + ":" + LDT.getSecond();
            topicDao.insertTopic(
                    id,
                    topicIn.getTitle(),
                    topicIn.getLabel(),
                    user,
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
            Random r = new Random();
            int addExp = r.nextInt(100)+50;
            int exp = userObj.getExp() + addExp;
            int expDiff = Exp.checkAndLevelUp(exp,level);
            if(expDiff >= 0){
                exp = expDiff;
                level++;
            }
            userDao.updateExp(user,exp,level);
            return ResultMsg.success(id,addExp);
        }
        return ResultMsg.error("CODE_ERROR");
    }

    public ResultMsg isPromiseToEditTopic(String token,int id){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        String topicUser = topicDao.findTopicUserBId(id);
        if(user.equals(topicUser)){
            return ResultMsg.success();
        }
        return ResultMsg.error("REJECT");
    }


    public ResultMsg findTopicInfoToEdit(String token,int id){
        ResultMsg r = isPromiseToEditTopic(token,id);
        if(r.getMsg().equals("REJECT")){
            return r;
        }
        TopicOut topicOut = topicDao.findTopicInfoById(id);
        if(topicOut == null){
            return ResultMsg.error("NULL_ERROR");
        }
        return ResultMsg.success(topicOut);
    }


    public ResultMsg updateTopic(HttpSession session,String token,TopicIn topicIn){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        ResultMsg r = isPromiseToEditTopic(token,topicIn.getId());
        if(r.getMsg().equals("REJECT")){
            return r;
        }
        String codeSession = (String) session.getAttribute("code");
        if(codeSession.equals(topicIn.getCode())){
            LocalDateTime LDT = LocalDateTime.now();
            String date = LDT.getYear() + "-" + LDT.getMonthValue() + "-" + LDT.getDayOfMonth() + " " + LDT.getHour() + ":" + LDT.getMinute() + ":" + LDT.getSecond();
            TopicOut topicOut = topicDao.findTopicInfoById(topicIn.getId());
            Topic topic = new Topic(
                    topicIn.getId(),
                    topicIn.getTitle(),
                    topicIn.getLabel(),
                    user,
                    null,
                    null,
                    date,
                    topicOut.getView(),
                    topicOut.getComment(),
                    topicIn.getVersion()
            );
            TopicItem topicItem = new TopicItem(
                    topicIn.getId(),
                    topicIn.getTopicTitle(),
                    topicIn.getEnTitle(),
                    topicIn.getSource(),
                    topicIn.getAuthor(),
                    topicIn.getLanguage(),
                    topicIn.getAddress(),
                    topicIn.getDownload(),
                    topicIn.getContent()
            );
            int status1 = topicDao.updateTopicExpectCommentAndView(topic);
            int status2 = topicDao.updateTopicItem(topicItem);
            return (status1+status2) == 2 ? ResultMsg.success() : ResultMsg.error("UPDATE_ERROR");
        }
        return ResultMsg.error("CODE_ERROR");
    }

    public ResultMsg deleteTopic(String token,int id) {
        ResultMsg r = isPromiseToEditTopic(token, id);
        if (r.getMsg().equals("REJECT")) {
            return r;
        }
        int status1 = topicDao.deleteTopicItem(id);
        int status2 = topicDao.deleteLikeLog(id);
        int status3 = topicDao.deleteCommentByTopicId(id);
        int status4 = topicDao.deleteTopic(id);
        return status1+status2+status3+status4 >= 2 ? ResultMsg.success() : ResultMsg.error("DELETE_ERROR");
    }

    public ResultMsg searchTopic(String key,String classification,int page){
        String newKey = "%" + key + "%";
        return ResultMsg.success(topicDao.searchByKey(newKey,classification,(page-1)*10),topicDao.searchTotalByKey(newKey,classification));
    }





}
