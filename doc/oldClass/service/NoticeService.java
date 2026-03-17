package com.starfall.service;

import com.starfall.dao.NoticeDao;
import com.starfall.entity.Notice;
import com.starfall.entity.ResultMsg;
import com.starfall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class NoticeService {
    @Autowired
    NoticeDao noticeDao;
    @Autowired
    RedisUtil redisUtil;

    public ResultMsg findAllNotice(){
        if(redisUtil.get("notices", List.class) != null){
            return ResultMsg.success(redisUtil.get("notices", List.class));
        }
        List<Notice> notices = noticeDao.findAllNotice();
        redisUtil.set("notices",notices, 1, TimeUnit.DAYS);
        return ResultMsg.success(notices);
    }
}
