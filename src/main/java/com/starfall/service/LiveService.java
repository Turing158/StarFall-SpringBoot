package com.starfall.service;

import cn.hutool.dfa.SensitiveUtil;
import com.starfall.Exception.ServiceException;
import com.starfall.annotation.RequireRole;
import com.starfall.dao.LiveDao;
import com.starfall.dao.UserDao;
import com.starfall.dao.redis.LiveRedis;
import com.starfall.entity.*;
import com.starfall.util.CodeUtil;
import com.starfall.util.JsonOperate;
import com.starfall.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LiveService {
    @Autowired
    LiveDao liveDao;
    @Autowired
    UserDao userDao;
    @Autowired
    LiveRedis liveRedis;
    @Autowired
    UserInteractionService userInteractionService;
    @Autowired
    UserNoticeService userNoticeService;
    @Autowired
    JwtUtil jwtUtil;

    public List<LiveBroadcastShow> findLive(int index ,String platform){
        return liveRedis.getRedisLiveShow(index);
    }

    public Pair<List<LiveBroadcastHistory>,Integer> findAllLiveByUser(String token, int page){
        String user = jwtUtil.getTokenField(token,"USER");
        return Pair.of(liveRedis.getRedisLiveHistory(user,page),liveRedis.getRedisLiveHistoryCount(user));
    }

    @Transactional
    public void appendLiveApply(String token,String url,String platform){
        String user = jwtUtil.getTokenField(token,"USER");
        if(liveDao.currentDayLiveCount(user) >= 1){
            throw new ServiceException("LIVE_APPLY_DAY_MAX","今天申请直播上限");
        }
        if(liveDao.existUrl(url) > 0){
            throw new ServiceException("LIVE_APPLY_EXIST","该直播已申请，不能重复申请");
        }
        LocalDateTime ldt = LocalDateTime.now();
        String id = "lvb" + ldt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS")) + CodeUtil.getCode(6);
        LiveBroadcast liveBroadcast =
                new LiveBroadcast(id,user,url,"","",platform,"",ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),0);
        liveRedis.clearRedisLiveHistory(user);
        liveRedis.setRedisLiveHistoryCount(user,true);
        int result = liveDao.insertLiveBroadcast(liveBroadcast);
        if(result <= 0){
            throw new ServiceException("DATA_ERROR","直播申请失败");
        }
    }

    @Transactional
    public void deleteLiveApply(String token,String id){
        String user = jwtUtil.getTokenField(token,"USER");
        LiveBroadcast liveBroadcast = liveDao.findLiveBroadcast(id);
        if(liveBroadcast == null){
            throw new ServiceException("LIVE_NOT_FOUND","直播申请不存在");
        }
        if(!liveBroadcast.getUser().equals(user)){
            throw new ServiceException("PERMISSION_DENIED","您没有权限删除该直播申请");
        }
        liveRedis.clearRedisLiveHistory(user);
        liveRedis.setRedisLiveHistoryCount(user,false);
        int result = liveDao.deleteLiveBroadcast(id);
        liveRedis.clearRedisLiveShow();
        if(result <= 0){
            throw new ServiceException("DATA_ERROR","直播申请删除失败");
        }
    }

    public Pair<List<LiveBroadcast>,Integer> findAllLiveApplyByStatus0(String token,int page){
        String user = jwtUtil.getTokenField(token,"USER");
        return Pair.of(liveDao.findAllLiveApplyByStatus0((page-1)*10),liveDao.countLiveApplyByStatus0());
    }

    @Transactional
    @RequireRole({"admin","live_moderator"})
    public void updateLiveStatus(String token,String id,String playUid,String reason,boolean status){
        String user = jwtUtil.getTokenField(token,"USER");
        var sensitiveWords = SensitiveUtil.getFoundAllSensitive(reason);
        if(!sensitiveWords.isEmpty()){
            throw new ServiceException("SENSITIVE_ERROR","包含敏感词");
        }
        LiveBroadcast liveBroadcast = liveDao.findLiveBroadcast(id);
        if(liveBroadcast == null){
            throw new ServiceException("LIVE_NOT_FOUND","直播申请不存在");
        }
        
        int result = liveDao.updateLiveStatus(id,user,playUid,status ? 1 : -1,reason);
        liveRedis.clearRedisLiveHistory(user);
        liveRedis.clearRedisLiveShow();
        LiveNoticeAction liveNoticeAction = new LiveNoticeAction(id,liveBroadcast.getUrl(),reason,user,status);
        userNoticeService.insertNotice(liveBroadcast.getUser(), UserNoticeType.live,"直播申请"+(status ? "已通过" : "不通过"), JsonOperate.toJson(liveNoticeAction,false));
//        messageService.SendMessage(token,liveBroadcast.getUser(),"您的直播申请("+liveBroadcast.getUrl()+")已被"+(status?"<span style='color:darkgreen'>通过</span>":"<span style='color:darkred'>拒绝</span>")+"，原因："+reason);
        if(result <= 0){
            throw new ServiceException("DATA_ERROR","直播申请状态更新失败");
        }
    }
}
