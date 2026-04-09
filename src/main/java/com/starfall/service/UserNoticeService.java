package com.starfall.service;

import cn.hutool.core.lang.Pair;
import com.starfall.dao.UserInteractionDao;
import com.starfall.dao.redis.UserInteractionRedis;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.TopicNoticeAction;
import com.starfall.entity.UserNotice;
import com.starfall.entity.UserNoticeType;
import com.starfall.util.CodeUtil;
import com.starfall.util.DateUtil;
import com.starfall.util.JsonOperate;
import com.starfall.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class UserNoticeService {
    @Autowired
    private UserInteractionDao userInteractionDao;
    @Autowired
    UserInteractionRedis userInteractionRedis;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private JwtUtil jwtUtil;

    //异步添加通知，并通过websocket推送
    @Transactional
    @Async
    public void insertNotice(String user, UserNoticeType type, String title, String action) {
        log.info("执行insertNotice，用户：{}，类型：{}，内容：{}，操作：{}",user,type,title,action);
        LocalDateTime ldt = LocalDateTime.now();
        String id = "un" + dateUtil.getDateTimeByFormat(ldt,"yyyyMMddHHmmssSSSS") + CodeUtil.getCode(6);
        String createTime = dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss");
        UserNotice userNotice = new UserNotice(id,user,createTime,title,type,0,action);
        userInteractionDao.insertUserNotice(userNotice);
        userInteractionRedis.setRedisUserNotice(user,userNotice,true);
        userInteractionRedis.setRedisLastUserNotice(user,userNotice);
        userInteractionRedis.setRedisUserNoticeUnreadCount(user,true);
        log.info("用户{}的通知{}已插入数据库，准备通过WebSocket推送",user,id);
        log.info("推送内容：{}", JsonOperate.toJson(userNotice));
        webSocketService.sendMessageToUser(user, JsonOperate.toJson(userNotice));
    }

    //异步辅助Topic的操作完成后，更新通知Action的状态，防止重复操作
    @Async
    @Transactional
    public void updateTopicNoticeActionHandle(UserNotice notice,String user){
        if(notice != null && notice.getUser().equals(user) && notice.getType().equals(UserNoticeType.topic)){
            var action = JsonOperate.toObject(notice.getAction(), TopicNoticeAction.class);
            if(!action.isHandle()){
                action.setHandle(true);
                notice.setAction(JsonOperate.toJson(action,false));
                updateNoticeAction(notice.getId(),notice.getAction());
                userInteractionRedis.setRedisUserNotice(notice);
            }
        }
    }

    //查询最后一条通知和未读数量
    public Pair<UserNotice, Integer> findLastNoticeAndUnreadNum(String token) {
        log.info("执行findLastNoticeAndUnreadNum");
        String user = jwtUtil.getTokenField(token,"USER");
        return Pair.of(userInteractionRedis.getRedisLastUserNotice(user), userInteractionRedis.getRedisUserNoticeUnreadCount(user));
    }

    //查询所有通知
    public Pair<List<UserNotice>,Integer> findAllUserNotice(int index, String token){
        log.info("执行findAllUserNotice:index={}",index);
        String user = jwtUtil.getTokenField(token,"USER");
        if(index < 0){
            index = 0;
        }
        return Pair.of(userInteractionRedis.getRedisUserNotice(user,index), userInteractionRedis.getRedisUserNoticeCount(user));
    }

    // 标记已读
    @Transactional
    public void markAsRead(List<UserNotice> userNotices, String token) {
        var ids = userNotices.stream().map(UserNotice::getId).toArray(String[]::new);
        log.info("执行markAsRead:userNotices={}",String.join(",",ids));
        String user = jwtUtil.getTokenField(token,"USER");
        if(userNotices.size() == 1){
            log.info("执行markAsRead单条:{}",userNotices.get(0).getId());
            userInteractionRedis.setRedisUserNoticeRead(user,ids[0]);
            userInteractionDao.updateUserNoticeStatus(ids[0],1);
        }
        else if(userNotices.size() > 1){
            log.info("执行markAsRead批量:{}",String.join(",",ids));
            userInteractionRedis.setRedisUserNoticeRead(user,ids);
            userInteractionDao.UpdateBatchUserNotice(userNotices);
        }
        else{
            log.info("执行markAsRead全部设置为已读");
            userInteractionRedis.setRedisUserNoticeRead(user);
            userInteractionDao.updateUserNoticeStatus1ByUser(user);
        }
    }

    //删除通知
    @Transactional
    public ResultMsg deleteNotice(String id, String token) {
        log.info("执行deleteNotice:id={}",id);
        String user = jwtUtil.getTokenField(token,"USER");
        if(userInteractionDao.findUserNoticeById(id).getType() == UserNoticeType.all){
            log.warn("用户{}尝试删除全局通知{}，已拒绝",user,id);
            return ResultMsg.error("NOT_OPERATE");
        }
        userInteractionDao.deleteUserNotice(id);
        log.info("用户{}删除通知{}",user,id);
        return ResultMsg.success();
    }

    //用于其他服务调用查找UserNotice
    public UserNotice findUserNoticeById(String id){
        return id != null ? userInteractionDao.findUserNoticeById(id) : null;
    }

    //用于调用修改UserNotice
    @Transactional
    public int updateNoticeAction(String id,String action){
        return userInteractionDao.updateUserNoticeAction(id,action);
    }
}
