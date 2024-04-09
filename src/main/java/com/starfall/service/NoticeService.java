package com.starfall.service;

import com.starfall.dao.NoticeDao;
import com.starfall.entity.Notice;
import com.starfall.entity.ResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {
    @Autowired
    NoticeDao noticeDao;
    @Autowired
    RedisTemplate redisTemplate;

    public ResultMsg findAllNotice(){
        ValueOperations<String,Object> redis = redisTemplate.opsForValue();
        if(redis.get("notices") != null){
            return ResultMsg.success(redis.get("notices"));
        }
        List<Notice> notices = noticeDao.findAllNotice();
        redis.set("notices",notices);
        return ResultMsg.success(notices);
    }
}
