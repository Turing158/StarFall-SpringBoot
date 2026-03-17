package com.starfall.controller;

import com.starfall.Exception.ParamException;
import com.starfall.entity.FriendApplication;
import com.starfall.entity.FriendRelation;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.admin.FriendRelationAdminVO;
import com.starfall.entity.admin.UserNoticeAdminVO;
import com.starfall.service.AdminUserInteractionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/starfall/console/admin")
public class AdminUserInteractionController {

    @Autowired
    AdminUserInteractionService adminUserInteractionService;

    @PostMapping("/userNotice/find")
    public ResultMsg findAllUserNotice(String user,int page,String type){
        log.info("查询用户通知，page={},type={}",page,type);
        var pair = adminUserInteractionService.findAllUserNotice(user,page,type);
        return ResultMsg.success(pair.getFirst(),pair.getSecond());
    }

    @PostMapping("/userNotice/insert")
    public ResultMsg insertUserNotice(@RequestBody UserNoticeAdminVO userNoticeAdminVO){
        log.info("插入用户通知，userNoticeAdminVO={}", userNoticeAdminVO);
        adminUserInteractionService.insertUserNotice(userNoticeAdminVO);
        return ResultMsg.success();
    }

    @PostMapping("/userNotice/update")
    public ResultMsg updateUserNotice(@RequestBody UserNoticeAdminVO userNoticeAdminVO){
        log.info("更新用户通知，userNoticeAdminVO={}", userNoticeAdminVO);
        adminUserInteractionService.updateUserNotice(userNoticeAdminVO);
        return ResultMsg.success();
    }

    @PostMapping("/userNotice/read")
    public ResultMsg updateUserNoticeRead(String id,boolean isRead){
        log.info("更新用户通知，id={},isRead={}", id,isRead);
        adminUserInteractionService.updateUserNoticeRead(id,isRead);
        return ResultMsg.success();
    }

    @PostMapping("/userNotice/delete")
    public ResultMsg deleteUserNotice(String id){
        log.info("删除用户通知，id={}", id);
        adminUserInteractionService.deleteUserNotice(id);
        return ResultMsg.success();
    }

    @PostMapping("/friendApplication/find")
    public ResultMsg findAllFriendApplication(String user,int page){
        log.info("查询好友申请，user={},page={}", user,page);
        var pair = adminUserInteractionService.findAllFriendApplication(user,page);
        return ResultMsg.success(pair.getFirst(),pair.getSecond());
    }

    @PostMapping("/friendApplication/insert")
    public ResultMsg insertFriendApplication(@RequestBody FriendApplication friendApplication){
        log.info("插入好友申请，friendApplication={}", friendApplication);
        adminUserInteractionService.insertFriendApplication(friendApplication);
        return ResultMsg.success();
    }

    @PostMapping("/friendApplication/update")
    public ResultMsg updateFriendApplication(@RequestBody FriendApplication friendApplication){
        log.info("更新好友申请，friendApplication={}", friendApplication);
        adminUserInteractionService.updateFriendApplication(friendApplication);
        return ResultMsg.success();
    }

    @PostMapping("/friendApplication/delete")
    public ResultMsg deleteFriendApplication(String id){
        log.info("删除好友申请，id={}", id);
        adminUserInteractionService.deleteFriendApplication(id);
        return ResultMsg.success();
    }

    @PostMapping("/friendRelation/find")
    public ResultMsg findAllFriendRelation(int page){
        log.info("查询好友关系，page={}", page);
        var pair = adminUserInteractionService.findAllFriendRelation(page);
        return ResultMsg.success(pair.getFirst(),pair.getSecond());
    }

    @PostMapping("/friendRelation/insert")
    public ResultMsg insertFriendRelation(@RequestBody List<FriendRelationAdminVO> friendRelationAdminVO){
        log.info("插入好友关系，friendRelationAdminVO={}", friendRelationAdminVO);
        if(friendRelationAdminVO.size() < 2){
            throw new ParamException("PARAM_ERROR","请提供两个用户的id");
        }
        adminUserInteractionService.insertFriendRelation(friendRelationAdminVO.get(0), friendRelationAdminVO.get(1));
        return ResultMsg.success();
    }

    @PostMapping("/friendRelation/update")
    public ResultMsg updateFriendRelation(@RequestBody FriendRelation friendRelation){
        log.info("更新好友关系，friendRelation={}", friendRelation);
        adminUserInteractionService.updateFriendRelation(friendRelation);
        return ResultMsg.success();
    }

    @PostMapping("/friendRelation/delete")
    public ResultMsg deleteFriendRelation(String id,boolean isDeleteOtherRelation) {
        log.info("删除好友关系，id={},isDeleteOtherRelation={}", id, isDeleteOtherRelation);
        adminUserInteractionService.deleteFriendRelation(id, isDeleteOtherRelation);
        return ResultMsg.success();
    }
}
