package com.starfall.dao.redis;

import com.starfall.dao.UserInteractionDao;
import com.starfall.entity.UserNotice;
import com.starfall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class UserInteractionRedis {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserInteractionDao userInteractionDao;

    final int userInteractionPageSize = 10;
    final int userInteractionCacheSize = 40;

    public List<UserNotice> getRedisUserNotice(String user, int index){
        List<UserNotice> notices;
        String key = redisUtil.joinKey("user:notices",user,"list:data");
        if(index < userInteractionCacheSize){
            if(redisUtil.hasKey(key)){
                var cache = redisUtil.getList(key,UserNotice.class);
                notices = redisUtil.paginateByIndex(cache,index,userInteractionPageSize);
            }
            else{
                var cache = userInteractionDao.findAllUserNotice(user,0,userInteractionPageSize);
                redisUtil.set(key,cache,1, TimeUnit.HOURS);
                notices = redisUtil.paginateByIndex(cache,index,userInteractionPageSize);
            }
        }
        else{
            notices = userInteractionDao.findAllUserNotice(user,index,userInteractionPageSize);
        }
        return notices;
    }

    public int getRedisUserNoticeCount(String user){
        int count;
        String key = redisUtil.joinKey("user:notices",user,"list:count");
        if(redisUtil.hasKey(key)){
            count = redisUtil.get(key,Integer.class);
        }
        else{
            count = userInteractionDao.countAllUserNotice(user);
            redisUtil.set(key,count,1, TimeUnit.HOURS);
        }
        return count;
    }

    public UserNotice getRedisLastUserNotice(String user){
        UserNotice lastNotice;
        String key = redisUtil.joinKey("user:notices",user,"last");
        if(redisUtil.hasKey(key)){
            lastNotice = redisUtil.get(key,UserNotice.class);
        }
        else{
            lastNotice = userInteractionDao.findLastNotice(user);
            redisUtil.set(key,lastNotice,1, TimeUnit.HOURS);
        }
        return lastNotice;
    }

    public int getRedisUserNoticeUnreadCount(String user){
        int count;
        String key = redisUtil.joinKey("user:notices",user,"unread");
        if(redisUtil.hasKey(key)){
            count = redisUtil.get(key,Integer.class);
        }
        else{
            count = userInteractionDao.findUnreadNum(user);
            redisUtil.set(key,count,1, TimeUnit.HOURS);
        }
        return count;
    }
}
