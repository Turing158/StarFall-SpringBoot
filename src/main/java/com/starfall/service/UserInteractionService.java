package com.starfall.service;

import cn.hutool.core.lang.Pair;
import com.starfall.Exception.ServiceException;
import com.starfall.dao.UserDao;
import com.starfall.dao.UserInteractionDao;
import com.starfall.entity.*;
import com.starfall.util.*;
import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserInteractionService {

    @Getter
    @Autowired
    UserInteractionDao userInteractionDao;
    @Autowired
    UserDao userDao;
    @Autowired
    WebSocketService webSocketService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    DateUtil dateUtil;


    //查询最后一条通知和未读数量
    public Pair<UserNotice, Integer> findLastNoticeAndUnreadNum(String token) {
        log.info("执行findLastNoticeAndUnreadNum");
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        return Pair.of(userInteractionDao.findLastNotice(user), userInteractionDao.findUnreadNum(user));
    }

    //查询所有通知
    public Pair<List<UserNotice>,Integer> findAllUserNotice(int index, String token){
        log.info("执行findAllUserNotice:index={}",index);
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        if(index < 0){
            index = 0;
        }
        return Pair.of(userInteractionDao.findAllUserNotice(index,user), userInteractionDao.countAllUserNotice(user));
    }

    // 标记已读
    @Transactional
    public void markAsRead(List<UserNotice> userNotices, String token) {
        log.info("执行markAsRead:userNotices={}",String.join(",",userNotices.stream().map(UserNotice::getId).toArray(String[]::new)));
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        if(userNotices.size() == 1){
            log.info("执行markAsRead单条:{}",userNotices.get(0).getId());
            userInteractionDao.updateUserNoticeStatus(userNotices.get(0).getId(),1);
        }
        else if(userNotices.size() > 1){
            log.info("执行markAsRead批量:{}",String.join(",",userNotices.stream().map(UserNotice::getId).toArray(String[]::new)));
            userInteractionDao.UpdateBatchUserNotice(userNotices);
        }
        else{
            log.info("执行markAsRead全部设置为已读");
            userInteractionDao.updateUserNoticeStatus1ByUser(user);
        }
    }

    //删除通知
    @Transactional
    public ResultMsg deleteNotice(String id, String token) {
        log.info("执行deleteNotice:id={}",id);
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        if(userInteractionDao.findUserNoticeById(id).getType() == UserNoticeType.all){
            log.warn("用户{}尝试删除全局通知{}，已拒绝",user,id);
            return ResultMsg.error("NOT_OPERATE");
        }
        userInteractionDao.deleteUserNotice(id);
        log.info("用户{}删除通知{}",user,id);
        return ResultMsg.success();
    }



    //异步添加通知，并通过websocket推送
    @Transactional
    @Async
    public void insertNotice(String user, UserNoticeType type,String title, String action) {
        log.info("执行insertNotice，用户：{}，类型：{}，内容：{}，操作：{}",user,type,title,action);
        LocalDateTime ldt = LocalDateTime.now();
        String id = "un" + dateUtil.getDateTimeByFormat(ldt,"yyyyMMddHHmmssSSSS") + CodeUtil.getCode(6);
        String createTime = dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss");
        UserNotice userNotice = new UserNotice(id,user,createTime,title,type,0,action);
        userInteractionDao.insertUserNotice(userNotice);
        log.info("用户{}的通知{}已插入数据库，准备通过WebSocket推送",user,id);
        log.info("推送内容：{}",JsonOperate.toJson(userNotice));
        webSocketService.sendMessageToUser(user, JsonOperate.toJson(userNotice));
    }

    //用于其他服务调用查找UserNotice
    public UserNotice findUserNoticeById(String id){
        return id != null ? userInteractionDao.findUserNoticeById(id) : null;
    }

    //用于其他服务调用修改UserNotice
    @Transactional
    public int updateNoticeAction(String id,String action){
        return userInteractionDao.updateUserNoticeAction(id,action);
    }

    //好友申请
    @Transactional
    public boolean appendFriendApplication(String token,String friend,String reason){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        log.info("执行appendFriendApplication:用户{}申请添加{}为好友，理由：{}",user,friend,reason);
        if(userDao.findByUser(friend) == null){
            log.warn("用户{}尝试添加不存在的用户{}为好友，已拒绝",user,friend);
            throw new ServiceException("USER_NOT_EXIST","用户不存在");
        }
        if(userInteractionDao.countFriendRelationWithFriend(user,friend) != 0){
            log.warn("用户{}尝试添加已是好友的用户{}为好友，已拒绝",user,friend);
            throw new ServiceException("ALREADY_FRIEND","用户已是好友");
        }
        if (userInteractionDao.countFriendApplicationAndStatus0(user,friend) != 0){
            log.warn("用户{}尝试重复添加{}为好友，已拒绝",user,friend);
            throw new ServiceException("APPLICATION_EXIST","好友申请已存在");
        }
        FriendApplication application = userInteractionDao.findFriendApplicationAndStatus0(friend,user);
        if(application != null){
            // 对方已经申请添加你为好友，直接接受申请
            log.warn("用户{}发现{}已经申请添加自己为好友，直接接受申请",user,friend);
            UserNotice userNotice = userInteractionDao.findUserNoticeByActionPart(user,"%"+"%");
            acceptApplication(user,userNotice,application,true);
            return false;
        }
        LocalDateTime ldt = LocalDateTime.now();
        String id = "fa" + dateUtil.getDateTimeByFormat(ldt,"yyyyMMddHHmmssSSSS") + CodeUtil.getCode(6);
        UserVO userObj = userDao.findByUser(user);
        String action = JsonOperate.toJson(new FriendNoticeAction(id,userObj.getName(),userObj.getUser(),userObj.getAvatar(),reason,0,false),false);
        log.info("生成好友申请通知的action：{}",action);
        insertNotice(friend,UserNoticeType.friend,"新的好友申请",action);
        FriendApplication friendApplication = new FriendApplication(
                id,
                user,
                friend,
                dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss"),
                reason,
                0
        );
        log.info("生成好友申请记录：id={}",id);
        userInteractionDao.insertFriendApplication(friendApplication);
        return true;
    }

    public Pair<List<Friend>,Integer> findAllFriend(int index, String token) {
        log.info("执行findAllFriend:index={}",index);
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        return Pair.of(userInteractionDao.findAllFriendsWithLastNotice(index,user), userInteractionDao.countFriendRelation(user));
    }

    public ResultMsg acceptApplication(String token,String noticeId,String applicationId,boolean accept){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        log.info("执行acceptApplication:用户{}，noticeId={},applicationId={},accept={}",user,noticeId,applicationId,accept);
        FriendApplication application = userInteractionDao.findFriendApplicationById(applicationId);
        if(application == null){
            log.warn("用户{}尝试处理不存在的好友申请{}，已拒绝",user,applicationId);
            return ResultMsg.error("APPLICATION_NOT_EXIST");
        }
        UserNotice userNotice = userInteractionDao.findUserNoticeById(noticeId);
        return acceptApplication(user,userNotice,application,accept);
    }

    @Transactional
    public ResultMsg acceptApplication(String user,UserNotice userNotice,FriendApplication application,boolean accept){
        log.info("此acceptApplication方法直接用于操作申请");
        if(!(application.getFromUser().equals(user) || application.getToUser().equals(user))){
            log.warn("用户{}尝试处理不属于自己的好友申请{}，已拒绝",user,application.getId());
            return ResultMsg.error("NO_PERMISSION");
        }
        log.info("notice的action:{}",userNotice.getAction());
        FriendNoticeAction friendNoticeAction = JsonOperate.toObject(userNotice.getAction(), FriendNoticeAction.class);
        if(application.getStatus() != 0){
            log.warn("用户{}尝试处理已经被处理过的好友申请{}",user,application.getId());
            if(!friendNoticeAction.isHandled()){
                log.warn("防止重复处理好友申请{}，已将通知{}的action标记为已处理",application.getId(),userNotice.getId());
                friendNoticeAction.setHandled(true);
                friendNoticeAction.setStatus(application.getStatus());
                log.info("更新通知{}的action为：true",userNotice.getId());
                userInteractionDao.updateUserNoticeAction(userNotice.getId(),JsonOperate.toJson(friendNoticeAction,false));
            }
            return ResultMsg.error("APPLICATION_ALREADY_PROCESSED");
        }
        //拒绝申请
        if(!accept){
            log.info("用户{}拒绝好友申请{}",user,application.getId());
            insertNotice(application.getFromUser(),UserNoticeType.friend,"拒绝添加好友","{\"user\":\""+user+"\",\"status\":-1}");
            userInteractionDao.updateFriendApplicationStatus(application.getId(),-1);
            friendNoticeAction.setHandled(true);
            friendNoticeAction.setStatus(-1);
            log.info("更新通知{}的action为：true，status为：-1",userNotice.getId());
            userInteractionDao.updateUserNoticeAction(userNotice.getId(),JsonOperate.toJson(friendNoticeAction,false));
            return ResultMsg.success(false);
        }
        //接受申请
        log.info("用户{}接受好友申请{}",user,application.getId());
        userInteractionDao.updateFriendApplicationStatus(application.getId(),1);
        LocalDateTime ldt = LocalDateTime.now();
        String date = dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss");
        String fromRelationId = "fr" + dateUtil.getDateTimeByFormat(ldt,"yyyyMMddHHmmssSSSS") + CodeUtil.getCode(6);
        String toRelationId = "fr" + dateUtil.getDateTimeByFormat(ldt,"yyyyMMddHHmmssSSSS") + CodeUtil.getCode(6);
        FriendRelation fromUserRelation = new FriendRelation(fromRelationId,application.getFromUser(),application.getToUser(),0,null,date,date,0);
        FriendRelation toUserRelation = new FriendRelation(toRelationId,application.getToUser(),application.getFromUser(),0,null,date,date,0);
        log.info("生成好友关系记录：fromRelationId={},toRelationId={}",fromRelationId,toRelationId);
        userInteractionDao.insertFriendRelation(fromUserRelation,toUserRelation);
        log.info("提示用户{}，{}已同意添加你为好友",user,application.getFromUser());
        insertNotice(application.getFromUser(),UserNoticeType.friend,"新好友","{\"user\":\""+user+"\",\"status\":1}");
        friendNoticeAction.setHandled(true);
        friendNoticeAction.setStatus(1);
        log.info("更新通知{}的action为：true，status为：1",userNotice.getId());
        userInteractionDao.updateUserNoticeAction(userNotice.getId(),JsonOperate.toJson(friendNoticeAction,false));
        return ResultMsg.success(true);
    }

    public List<Message> getMsgByFriend(String token,String friend,int index){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        return userInteractionDao.findMsgByToUserAndFromUser(user,friend,index);
    }

    @Transactional
    public Message SendMessage(String token,String toUser,String content){
        Claims claims = JwtUtil.parseJWT(token);
        String fromUser = (String) claims.get("USER");
        if (Objects.equals(fromUser, toUser)){
            throw new ServiceException("DISABLE_TO_SELF","不能发送消息给自己");
        }
        if(userDao.existUser(toUser) == 0){
            throw new ServiceException("NO_EXIST_USER","用户"+toUser+"不存在");
        }
        UserVO fromUserObj = getUserObj(token,fromUser);
        if(fromUserObj == null){
            throw new ServiceException("USER_ERROR","用户"+fromUser+"不存在，且token存在伪造问题");
        }
        FriendRelation friendRelation = userInteractionDao.findFriendRelation(toUser,fromUser);
        if(friendRelation.getRelation() == -1){
            throw new ServiceException("IS_BLACK","对方已将你加入黑名单，无法发送消息");
        }
        if(content.contains("[&divide&]")){
            content = content.replace("[&divide&]"," ");
        }
        User toUserObj= userDao.findByUserOrEmail(toUser);
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Message message = new Message(fromUser,friendRelation.getAlias() == null || friendRelation.getAlias().isEmpty() ? fromUserObj.getName() : friendRelation.getAlias(),fromUserObj.getAvatar(),toUser,toUserObj.getName(),toUserObj.getAvatar(),date,content);
        List<Message> fromUserMsgs = userInteractionDao.findFromUserMsgByFromUserAndToUser(fromUser,toUser);
        if(fromUserMsgs.isEmpty()){
//          直接保存新数据
            userInteractionDao.insertMsg(message);
            return friendRelation.getRelation() == 1 ? message : null;
        }
        Message fromUserMsg = fromUserMsgs.get(0);
        String oldDateTimeStr = fromUserMsg.getDate();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime oldDateTime = LocalDateTime.parse(oldDateTimeStr,df);
        LocalDateTime newDateTime = LocalDateTime.parse(date,df);
        if(oldDateTime.plusMinutes(1).isAfter(newDateTime)){
//          更新
            fromUserMsg.setContent(fromUserMsg.getContent()+"[&divide&]"+content);
            userInteractionDao.updateMsgContent(fromUser,toUser,oldDateTimeStr,fromUserMsg.getContent());
            return friendRelation.getRelation() == 1 ? fromUserMsg : null;
        }
        // 直接保存新数据
        userInteractionDao.insertMsg(message);
        return friendRelation.getRelation() == 1 ? message : null;
    }

    public FriendRelation handleFriendExist(String token,String friend){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        FriendRelation relation = userInteractionDao.findFriendRelation(user, friend);
        if(relation == null){
            log.warn("用户{}尝试修改与{}的好友备注，但好友关系不存在",user,friend);
            throw new ServiceException("FRIEND_NOT_EXIST","好友关系不存在");
        }
        return relation;
    }

    @Transactional
    public void updateFriendAlias(String token,String friend,String alias){
        FriendRelation relation = handleFriendExist(token,friend);
        log.info("当前好友备注：{}",relation.getAlias());
        if(alias.equals(relation.getAlias())){
            return;
        }
        log.info("执行updateFriendAlias:用户{}，friend={},alias={}",relation.getFromUser(),friend,alias);
        LocalDateTime ldt = LocalDateTime.now();
        userInteractionDao.updateFriendAlias(relation.getFromUser(),friend,alias,dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss"));
    }

    @Transactional
    public void updateFriendTop(String token,String friend,boolean isTop){
        FriendRelation relation = handleFriendExist(token,friend);
        if(relation.getIsTop() == (isTop ? 1 : 0)){
            return;
        }
        log.info("执行updateFriendTop:用户{}，friend={},isTop={}",relation.getFromUser(),friend,isTop);
        LocalDateTime ldt = LocalDateTime.now();
        userInteractionDao.updateFriendIsTop(relation.getFromUser(),friend,isTop ? 1 : 0,dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss"));
    }

    @Transactional
    public void updateFriendRelation(String token,String friend,int relation){
        FriendRelation friendRelation = handleFriendExist(token,friend);
        if(friendRelation.getRelation() == relation){
            return;
        }
        log.info("执行updateFriendRelation:用户{}，friend={},relation={}",friendRelation.getFromUser(),friend,relation);
        LocalDateTime ldt = LocalDateTime.now();
        userInteractionDao.updateFriendRelation(friendRelation.getFromUser(),friend,relation,dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss"));
    }

    @Transactional
    public void deleteFriend(String token,String friend,boolean deleteChatRecord){
        FriendRelation relation = handleFriendExist(token,friend);
        log.info("执行deleteFriend:用户{}，friend={}",relation.getFromUser(),friend);
        FriendRelation toRelation = userInteractionDao.findFriendRelation(relation.getToUser(), relation.getFromUser());
        var userObj = getUserObj(token,relation.getFromUser());
        userInteractionDao.deleteFriendRelation(relation.getFromUser(),friend);
        if(deleteChatRecord){
            userInteractionDao.deleteMsgByUserAndFriend(relation.getFromUser(),friend);
        }
        FriendDeleteNoticeAction friendDeleteNoticeAction = new FriendDeleteNoticeAction(relation.getFromUser(),userObj.getName(),toRelation.getAlias(),deleteChatRecord);
        insertNotice(relation.getFromUser(),UserNoticeType.friend,"失去了一个好友",JsonOperate.toJson(friendDeleteNoticeAction,false));
    }

    private UserVO getUserObj(String token,String user){
        if(redisUtil.hasKey("onlineUser:" + token)){
            return redisUtil.get("onlineUser:" + token, UserVO.class);
        }
        return userDao.findByUserOrEmail(user).toUserVO();
    }
}
