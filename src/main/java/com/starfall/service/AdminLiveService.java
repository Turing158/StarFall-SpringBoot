package com.starfall.service;

import com.starfall.dao.AdminLiveDao;
import com.starfall.entity.LiveBroadcast;
import com.starfall.entity.LiveBroadcastHistory;
import com.starfall.entity.ResultMsg;
import com.starfall.util.CodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AdminLiveService {

    @Autowired
    private AdminLiveDao adminLiveDao;

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
        return ResultMsg.success(adminLiveDao.insertLive(live));
    }

    public ResultMsg updateLive(LiveBroadcast live){
        return ResultMsg.success(adminLiveDao.updateLive(live));
    }

    public ResultMsg deleteLive(String id){
        return ResultMsg.success(adminLiveDao.deleteLive(id));
    }

}
