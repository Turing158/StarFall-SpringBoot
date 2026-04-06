package com.starfall.service;

import cn.hutool.dfa.SensitiveUtil;
import com.starfall.Exception.ServiceException;
import com.starfall.dao.HomeDao;
import com.starfall.dao.redis.HomeRedis;
import com.starfall.dao.redis.UserRedis;
import com.starfall.entity.*;
import com.starfall.util.CodeUtil;
import com.starfall.util.DateUtil;
import com.starfall.util.JwtUtil;
import com.starfall.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class HomeService {
    @Autowired
    private HomeDao homeDao;
    @Autowired
    private HomeRedis homeRedis;
    @Autowired
    UserRedis userRedis;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    DateUtil dateUtil;

    public List<HomeTalk> findAllHomeTalk(int num) {
        int count = homeRedis.getRedisHomeTalkCount();
        if(count < num){
            return List.of();
        }
        return homeRedis.getRedisHomeTalks(num);
    }

    @Transactional
    public HomeTalk publicHomeTalk(String content,String token){
        String user = jwtUtil.getTokenField(token,"USER");

        LocalDateTime ldt = LocalDateTime.now();
        HomeTalk talk = homeRedis.getRedisHomeTalkMapper(user,dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss"));
        if(talk != null){
            LocalDateTime oldLdt = LocalDateTime.parse(talk.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if(oldLdt.toLocalDate().equals(ldt.toLocalDate())){
                throw new ServiceException("REPEATED","重复发布");
            }
        }
        var sensitiveWords = SensitiveUtil.getFoundAllSensitive(content);
        if(!sensitiveWords.isEmpty()){
            throw new ServiceException("SENSITIVE_ERROR","包含敏感词");
        }
//        HT202509151044492283c6ou
        User userObj = userRedis.findRedisUser(user);
        var homeTalk = new HomeTalk(
                "HT" + dateUtil.getDateTimeByFormat(ldt,"yyyyMMddHHmmssSSSS") + CodeUtil.getCode(4),
                user,
                userObj.getName(),
                userObj.getAvatar(),
                content,
                dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss")
        );
        homeRedis.setRedisHomeTalkCount(true);
        homeDao.insertHomeTalk(homeTalk);
        homeRedis.setRedisHomeTalk(homeTalk,true);
        homeRedis.setRedisHomeTalkMapper(homeTalk,true);
        homeRedis.setRedisHomeTalks(homeTalk,true);

        return homeTalk;
    }

    public ResultMsg deleteHomeTalk(String id,String token){
        String user = jwtUtil.getTokenField(token,"USER");
        HomeTalk talk = homeRedis.getRedisHomeTalk(id);
        if (talk != null && talk.getUser().equals(user)){
            homeRedis.setRedisHomeTalkCount(false);
            int status = homeDao.deleteHomeTalk(id);
            homeRedis.setRedisHomeTalk(talk,false);
            homeRedis.setRedisHomeTalkMapper(talk,false);
            homeRedis.setRedisHomeTalks(talk,false);
            return status == 1 ? ResultMsg.success() : ResultMsg.error("ERROR");
        }
        return ResultMsg.error("NOT_EXIST");
    }

    public List<Advertisement> findAdvertisementByPosition(String position){
        return homeRedis.getRedisAdvertisementByPosition(position);
    }

    public List<Notice> findAllNotice(){
        return homeRedis.getRedisNotice();
    }
}
