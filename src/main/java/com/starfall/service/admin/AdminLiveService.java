package com.starfall.service.admin;

import com.starfall.dao.admin.AdminLiveDao;
import com.starfall.entity.LiveBroadcast;
import com.starfall.entity.ResultMsg;
import com.starfall.util.CodeUtil;
import com.starfall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AdminLiveService {

    @Autowired
    private AdminLiveDao adminLiveDao;
    @Autowired
    private RedisUtil redisUtil;

    public ResultMsg findAllLive(int page){
        return ResultMsg.success(adminLiveDao.findAllLive(page),adminLiveDao.countAllLive());
    }

    public ResultMsg findLiveById(String id){
        return ResultMsg.success(adminLiveDao.findLiveById(id));
    }

    public ResultMsg insertLive(LiveBroadcast live){
        LocalDateTime ldt = LocalDateTime.now();
        live.setApplyTime(ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        live.setId("lvb" + ldt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS")) + CodeUtil.getCode(6));
        redisUtil.deleteBatchAsync("live:*");
        return ResultMsg.success(adminLiveDao.insertLive(live));
    }

    public ResultMsg updateLive(LiveBroadcast live){
        adminLiveDao.updateLive(live);
        redisUtil.deleteBatchAsync("live:*");
        return ResultMsg.success();
    }

    public ResultMsg deleteLive(String id){
        adminLiveDao.deleteLive(id);
        redisUtil.deleteBatchAsync("live:*");
        return ResultMsg.success();
    }

}
