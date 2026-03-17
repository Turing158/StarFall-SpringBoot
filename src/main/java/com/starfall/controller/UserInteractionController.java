package com.starfall.controller;

import com.starfall.Exception.ParamException;
import com.starfall.Exception.ServiceException;
import com.starfall.entity.Message;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.UserNotice;
import com.starfall.entity.UserNoticeType;
import com.starfall.service.UserInteractionService;
import com.starfall.service.WebSocketService;
import com.starfall.util.JsonOperate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserInteractionController {

    @Autowired
    UserInteractionService userInteractionService;
    @Autowired
    WebSocketService webSocketService;

    @PostMapping("/notice/last")
    public ResultMsg findLastNoticeAndUnreadNum(@RequestHeader("Authorization") String token){
        var pair = userInteractionService.findLastNoticeAndUnreadNum(token);
        return ResultMsg.success(pair.getKey(),pair.getValue());
    }

    @PostMapping("/notice/all")
    public ResultMsg findAllUserNotice(int index,@RequestHeader("Authorization") String token){
        var pair = userInteractionService.findAllUserNotice(index, token);
        return ResultMsg.success(pair.getKey(),pair.getValue());
    }

    @PostMapping("/notice/mark")
    public ResultMsg markAsRead(@RequestBody List<UserNotice> notices,@RequestHeader("Authorization") String token){
        userInteractionService.markAsRead(notices, token);
        return ResultMsg.success();
    }

    @PostMapping("/friend/application/send")
    public ResultMsg applyFriend(@RequestHeader("Authorization") String token,String friend,String reason){
        return userInteractionService.appendFriendApplication(token, friend, reason) ? ResultMsg.success() : ResultMsg.warning("SUCCESS_ADD");
    }

    @PostMapping("/friend/application/handle")
    public ResultMsg acceptFriendApplication(@RequestHeader("Authorization") String token,String noticeId,String applicationId,boolean accept) {
         return userInteractionService.acceptApplication(token, noticeId, applicationId, accept);
     }

     @PostMapping("/friend/all")
    public ResultMsg findAllFriend(@RequestHeader("Authorization") String token,int index){
        var friends = userInteractionService.findAllFriend(index,token);
        return ResultMsg.success(friends);
    }

    @PostMapping("/friend/msg/get")
    public ResultMsg findMsgByFriend(@RequestHeader("Authorization") String token,String friend,int index){
        List<Message> messages = userInteractionService.getMsgByFriend(token,friend,index);
        return ResultMsg.success(messages);
    }


    @PostMapping("/friend/msg/send")
    public ResultMsg sendMessage(@RequestHeader("Authorization") String token,String friend,String content){
        if(friend.isEmpty()){
            throw new ParamException("PARAM_NULL","好友不能为空");
        }
        if (content.isEmpty()){
            throw new ParamException("PARAM_NULL","内容不能为空");
        }
        Message msg = userInteractionService.SendMessage(token,friend,content);
        msg.setContent(content);
        webSocketService.sendMessageToUser(msg.getToUser(), JsonOperate.toJson(msg));
        return ResultMsg.success(msg);
    }

    @PostMapping("/friend/set/alias")
    public ResultMsg updateFriendAlias(@RequestHeader("Authorization") String token,String friend,String alias){
        userInteractionService.updateFriendAlias(token,friend,alias);
        return ResultMsg.success();
    }

    @PostMapping("/friend/set/top")
    public ResultMsg updateFriendTop(@RequestHeader("Authorization") String token,String friend,boolean isTop){
        userInteractionService.updateFriendTop(token,friend,isTop);
        return ResultMsg.success();
    }

    @PostMapping("/friend/set/relation")
    public ResultMsg updateFriendRelation(@RequestHeader("Authorization") String token,String friend,int relation){
        if(!Arrays.asList(0,1,-1).contains(relation)){
            throw new ParamException("PARAM_ERROR","好友关系参数错误");
        }
        userInteractionService.updateFriendRelation(token,friend,relation);
        return ResultMsg.success();
    }

    @PostMapping("/friend/delete")
    public ResultMsg deleteFriend(@RequestHeader("Authorization") String token,String friend,boolean deleteChatRecord){
        userInteractionService.deleteFriend(token,friend,deleteChatRecord);
        return ResultMsg.success();
     }
}
