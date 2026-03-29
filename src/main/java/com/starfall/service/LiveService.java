package com.starfall.service;

import cn.hutool.dfa.SensitiveUtil;
import com.starfall.Exception.ServiceException;
import com.starfall.annotation.RequireRole;
import com.starfall.dao.LiveDao;
import com.starfall.dao.UserDao;
import com.starfall.entity.*;
import com.starfall.util.CodeUtil;
import com.starfall.util.JsonOperate;
import com.starfall.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LiveService {
    @Autowired
    LiveDao liveDao;
    @Autowired
    UserDao userDao;
    @Autowired
    UserInteractionService userInteractionService;
    @Autowired
    UserNoticeService userNoticeService;
    @Autowired
    JwtUtil jwtUtil;

    public ResultMsg findLive(int index ,String platform){
        return ResultMsg.success(liveDao.findAllLiveShow(index));
    }

    public ResultMsg findAllLiveByUser(String token,int page){
        String user = jwtUtil.getTokenField(token,"USER");
        return ResultMsg.success(liveDao.findAllLiveByUser(user,(page-1)*10),liveDao.countLiveByUser(user));
    }

    @Transactional
    public ResultMsg appendLiveApply(String token,String url,String platform){
        String user = jwtUtil.getTokenField(token,"USER");
        if(liveDao.currentDayLiveCount(user) >= 1){
            return ResultMsg.error("LIVE_APPLY_DAY_MAX");
        }
        if(liveDao.existUrl(url) > 0){
            return ResultMsg.error("LIVE_APPLY_EXIST");
        }
        LocalDateTime ldt = LocalDateTime.now();
        String id = "lvb" + ldt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS")) + CodeUtil.getCode(6);
        LiveBroadcast liveBroadcast =
                new LiveBroadcast(id,user,url,"","",platform,"",ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),0);
        int result = liveDao.insertLiveBroadcast(liveBroadcast);
        return result > 0 ? ResultMsg.success() : ResultMsg.error("DATA_ERROR");
    }

    @Transactional
    public ResultMsg deleteLiveApply(String token,String id){
        String user = jwtUtil.getTokenField(token,"USER");
        LiveBroadcast liveBroadcast = liveDao.findLiveBroadcast(id);
        if(liveBroadcast == null){

            return ResultMsg.error("LIVE_NOT_FOUND");
        }
        if(!liveBroadcast.getUser().equals(user)){
            return ResultMsg.error("PERMISSION_DENIED");
        }
        int result = liveDao.deleteLiveBroadcast(id);
        return result > 0 ? ResultMsg.success() : ResultMsg.error("DATA_ERROR");
    }

    public ResultMsg findAllLiveApplyByStatus0(String token,int page){
        String user = jwtUtil.getTokenField(token,"USER");
        return ResultMsg.success(liveDao.findAllLiveApplyByStatus0((page-1)*10),liveDao.countLiveApplyByStatus0());
    }

    @Transactional
    @RequireRole({"admin","live_moderator"})
    public ResultMsg updateLiveStatus(String token,String id,String playUid,String reason,boolean status){
        String user = jwtUtil.getTokenField(token,"USER");
        var sensitiveWords = SensitiveUtil.getFoundAllSensitive(reason);
        if(!sensitiveWords.isEmpty()){
            throw new ServiceException("SENSITIVE_ERROR","包含敏感词");
        }
        LiveBroadcast liveBroadcast = liveDao.findLiveBroadcast(id);
        if(liveBroadcast == null){
            return ResultMsg.error("LIVE_NOT_FOUND");
        }
        int result = liveDao.updateLiveStatus(id,user,playUid,status ? 1 : -1,reason);
        LiveNoticeAction liveNoticeAction = new LiveNoticeAction(id,liveBroadcast.getUrl(),reason,user,status);
        userNoticeService.insertNotice(user, UserNoticeType.live,"直播申请"+(status ? "已通过" : "不通过"), JsonOperate.toJson(liveNoticeAction,false));
//        messageService.SendMessage(token,liveBroadcast.getUser(),"您的直播申请("+liveBroadcast.getUrl()+")已被"+(status?"<span style='color:darkgreen'>通过</span>":"<span style='color:darkred'>拒绝</span>")+"，原因："+reason);
        return result > 0 ? ResultMsg.success() : ResultMsg.error("DATA_ERROR");
    }
}
