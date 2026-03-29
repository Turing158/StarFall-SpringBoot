package com.starfall.service;

import cn.hutool.dfa.SensitiveUtil;
import com.starfall.Exception.ServiceException;
import com.starfall.dao.HomeDao;
import com.starfall.dao.UserDao;
import com.starfall.entity.*;
import com.starfall.util.CodeUtil;
import com.starfall.util.JwtUtil;
import com.starfall.util.RedisUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.ldap.PagedResultsControl;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class HomeService {
    @Autowired
    private HomeDao homeDao;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    JwtUtil jwtUtil;

    public List<HomeTalk> findAllHomeTalk(int num) {
        List<HomeTalk> homeTalks;
        int count = 0;
        if(redisUtil.hasKey("homeTalks:cache:count")){
            count = redisUtil.get("homeTalks:cache:count", Integer.class);
        }
        else {
            count = homeDao.countHomeTalk();
            redisUtil.set("homeTalks:cache:count", count, 1, TimeUnit.HOURS,true);
        }
        if(count < num){
            return null;
        }
        if(redisUtil.hasKey("homeTalks:cache:last80")){
            List<HomeTalk> cache = redisUtil.get("homeTalks:cache:last80", List.class);
            if(num > cache.size()){
                homeTalks = homeDao.findAllHomeTalk(num);
            }
            else{

                homeTalks = redisUtil.paginateByIndex(cache, num, 20);
            }
        }
        else{
            homeTalks = homeDao.findAllHomeTalk(num);
            redisUtil.set("homeTalks:cache:last80", homeTalks, 1, TimeUnit.HOURS);
        }
        return homeTalks;
    }

    @Transactional
    public void publicHomeTalk(String content,String token){
        String user = jwtUtil.getTokenField(token,"USER");

        LocalDateTime ldt = LocalDateTime.now();
        HomeTalk talk;
        if(redisUtil.hasKey("homeTalk:user:"+user+":"+ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))){
            talk = redisUtil.get("homeTalk:user:"+user+":"+ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),HomeTalk.class);
        }
        else{
            talk = homeDao.findHomeTalk(user);
        }

        if(talk != null){
            LocalDateTime oldLdt = LocalDateTime.parse(talk.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if(oldLdt.getDayOfWeek() == ldt.getDayOfWeek()){
                throw new ServiceException("REPEATED","重复发布");
            }
        }
        var sensitiveWords = SensitiveUtil.getFoundAllSensitive(content);
        if(!sensitiveWords.isEmpty()){
            throw new ServiceException("SENSITIVE_ERROR","包含敏感词");
        }
        var homeTalk = new HomeTalk(user,content,ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        redisUtil.set("homeTalk:user:"+user+":"+ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),talk, 1, TimeUnit.DAYS);
        if(redisUtil.hasKey("homeTalks:cache:last80")){
                List<HomeTalk> cache = redisUtil.get("homeTalks:cache:last80", List.class);
                cache.add(0,homeTalk);
            redisUtil.set("homeTalks:cache:last80",cache , 1, TimeUnit.HOURS);
        }
        homeDao.insertHomeTalk(homeTalk);
        if(redisUtil.hasKey("homeTalks:cache:count")){
            redisUtil.opsForValue().increment("homeTalks:cache:count",1);
        }
    }

    public ResultMsg deleteHomeTalk(String date,String token){
        String user = jwtUtil.getTokenField(token,"USER");
        HomeTalk talk = homeDao.findHomeTalkByUserAndDate(user,date);
        if (talk != null){
            int status = homeDao.deleteHomeTalk(user,date);
            return status == 1 ? ResultMsg.success() : ResultMsg.error("ERROR");
        }
        return ResultMsg.error("NOT_EXIST");
    }

    public List<Advertisement> findAdvertisementByPosition(String position){
        if(redisUtil.hasKey("advertisements:position:"+position)){
            List<Advertisement> advertisements = redisUtil.get("advertisements:position:"+position, List.class);
            return advertisements;
        }
        var advertisements = homeDao.findAdvertisementByPosition(position);
        redisUtil.set("advertisements:position:"+position,advertisements, 1, TimeUnit.DAYS);
        return advertisements;
    }

    public List<Notice> findAllNotice(){
        if(redisUtil.hasKey("notices:cache")){
            List<Notice> notices = redisUtil.get("notices:cache", List.class);
            return notices;
        }
        var notices = homeDao.findAllNotice();
        redisUtil.set("notices:cache",notices, 1, TimeUnit.DAYS);
        return notices;
    }
}
