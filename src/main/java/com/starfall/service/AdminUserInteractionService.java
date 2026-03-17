package com.starfall.service;

import com.starfall.Exception.AdminServiceException;
import com.starfall.dao.AdminUserInteractionDao;
import com.starfall.entity.FriendApplication;
import com.starfall.entity.FriendRelation;
import com.starfall.entity.UserNoticeType;
import com.starfall.entity.admin.FriendRelationAdminVO;
import com.starfall.entity.admin.UserNoticeAdminVO;
import com.starfall.util.CodeUtil;
import com.starfall.util.DateUtil;
import com.starfall.util.JsonOperate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUserInteractionService {

    @Autowired
    AdminUserInteractionDao adminUserInteractionDao;
    @Autowired
    WebSocketService webSocketService;
    @Autowired
    DateUtil dateUtil;

    public Pair<List<UserNoticeAdminVO>,Integer> findAllUserNotice(String user,int page, String type) {
        List<UserNoticeAdminVO> list;
        int num;
        if(type.equals("")){
            list = adminUserInteractionDao.findAllUserNotice(user,(page-1)*10);
            num = adminUserInteractionDao.countAllUserNotice(user);
        }
        else if(type.equals("all")){
            list = adminUserInteractionDao.findAllUserNoticeByTypeAll((page-1)*10);
            num = adminUserInteractionDao.countAllUserNoticeByTypeAll();
        }
        else{
            list = adminUserInteractionDao.findAllUserNoticeByType(user,(page-1)*10,type);
            num = adminUserInteractionDao.countAllUserNoticeByType(user,type);
        }
        return Pair.of(list,num);
    }

    @Transactional
    public void insertUserNotice(UserNoticeAdminVO userNoticeAdminVO){
        userNoticeAdminVO.setId("un" + dateUtil.getDateTimeByFormat("yyyyMMddHHmmssSSSS") + CodeUtil.getCode(6));
        adminUserInteractionDao.insertUserNotice(userNoticeAdminVO);
        if(userNoticeAdminVO.isSendNotice()){
            if(userNoticeAdminVO.getType() == UserNoticeType.all){
                webSocketService.sendMessageAll(JsonOperate.toJson(userNoticeAdminVO));
            }
            else{
                webSocketService.sendMessageToUser(userNoticeAdminVO.getUser(),JsonOperate.toJson(userNoticeAdminVO));
            }
        }
    }

    @Transactional
    public void updateUserNotice(UserNoticeAdminVO userNoticeAdminVO){
        adminUserInteractionDao.updateUserNotice(userNoticeAdminVO);
    }

    @Transactional
    public void updateUserNoticeRead(String id,boolean isRead){
        adminUserInteractionDao.updateUserNoticeRead(id,isRead ? 1 : 0);
    }

    @Transactional
    public void deleteUserNotice(String id){
        adminUserInteractionDao.deleteUserNotice(id);
    }

    public Pair<List<FriendApplication>,Integer> findAllFriendApplication(String user,int page){
        return Pair.of(adminUserInteractionDao.findAllFriendApplication(user,(page-1)*10),adminUserInteractionDao.countAllFriendApplication(user));
    }

    @Transactional
    public void insertFriendApplication(FriendApplication friendApplication){
        if(adminUserInteractionDao.countFriendApplicationByFromUserAndToUserAndStatus0(friendApplication.getFromUser(),friendApplication.getToUser()) != 0){
            throw new AdminServiceException("EXIST_APPLICATION","已经存在未处理的好友申请");
        }
        if(adminUserInteractionDao.countFriendApplicationByFromUserAndToUserAndStatus0(friendApplication.getToUser(),friendApplication.getFromUser()) != 0){
            throw new AdminServiceException("TO_USER_EXIST_APPLICATION","对方用户已经存在未处理的好友申请");
        }
        friendApplication.setId("fa" + dateUtil.getDateTimeByFormat("yyyyMMddHHmmssSSSS") + CodeUtil.getCode(6));
        adminUserInteractionDao.insertFriendApplication(friendApplication);
    }

    @Transactional
    public void updateFriendApplication(FriendApplication friendApplication){
        if(adminUserInteractionDao.countFriendApplicationByFromUserAndToUserAndStatus0(friendApplication.getToUser(),friendApplication.getFromUser()) != 0){
            throw new AdminServiceException("TO_USER_EXIST_APPLICATION","对方用户已经存在未处理的好友申请");
        }
        adminUserInteractionDao.updateFriendApplication(friendApplication);
    }

    @Transactional
    public void deleteFriendApplication(String id){
        adminUserInteractionDao.deleteFriendApplication(id);
    }

    public Pair<List<FriendRelationAdminVO>,Integer> findAllFriendRelation(int page){
        return Pair.of(adminUserInteractionDao.findAllFriendRelation((page-1)*10),adminUserInteractionDao.countAllFriendRelation());
    }

    @Transactional
    public void insertFriendRelation(FriendRelationAdminVO user1FriendRelation, FriendRelationAdminVO user2FriendRelation){
        if(adminUserInteractionDao.countFriendRelation(user1FriendRelation.getFromUser(),user2FriendRelation.getFromUser()) != 0){
            throw new AdminServiceException("EXIST_RELATION","已经存在好友关系");
        }
        user1FriendRelation.setId("fr" + dateUtil.getDateTimeByFormat("yyyyMMddHHmmssSSSS") + CodeUtil.getCode(6));
        user2FriendRelation.setId("fr" + dateUtil.getDateTimeByFormat("yyyyMMddHHmmssSSSS") + CodeUtil.getCode(6));
        user1FriendRelation.setCreateTime(dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
        user1FriendRelation.setUpdateTime(dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
        user2FriendRelation.setCreateTime(dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
        user2FriendRelation.setUpdateTime(dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
        adminUserInteractionDao.insertFriendRelation(user1FriendRelation,user2FriendRelation);
    }

    @Transactional
    public void updateFriendRelation(FriendRelation friendRelation){
        friendRelation.setUpdateTime(dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
        adminUserInteractionDao.updateFriendRelation(friendRelation);
    }

    @Transactional
    public void deleteFriendRelation(String id,boolean isDeleteOtherRelation){
        if(isDeleteOtherRelation){
            String otherId = adminUserInteractionDao.findOtherFriendRelationById(id);
            if(otherId != null){
                adminUserInteractionDao.deleteFriendRelation(otherId);
            }
        }
        adminUserInteractionDao.deleteFriendRelation(id);
    }
}
