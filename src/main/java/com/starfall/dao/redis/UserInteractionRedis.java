package com.starfall.dao.redis;

import com.starfall.dao.UserInteractionDao;
import com.starfall.entity.FriendRelation;
import com.starfall.entity.UserNotice;
import com.starfall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UserInteractionRedis {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserInteractionDao userInteractionDao;

    final int userNoticePageSize = 10;
    final int userNoticeCacheSize = 40;

    public List<UserNotice> getRedisUserNotice(String user, int index){
        List<UserNotice> notices;
        String key = redisUtil.joinKey("user:notices",user,"list:data");
        if(index < userNoticeCacheSize){
            if(redisUtil.hasKey(key)){
                var cache = redisUtil.getList(key,UserNotice.class);
                notices = redisUtil.paginateByIndex(cache,index,userNoticePageSize);
            }
            else{
                var cache = userInteractionDao.findAllUserNotice(user,0,userNoticePageSize);
                redisUtil.set(key,cache,1, TimeUnit.HOURS);
                notices = redisUtil.paginateByIndex(cache,index,userNoticePageSize);
            }
        }
        else{
            notices = userInteractionDao.findAllUserNotice(user,index,userNoticePageSize);
        }
        return notices;
    }

    public void setRedisUserNotice(String user,UserNotice notice,boolean increment){
        String key = redisUtil.joinKey("user:notices",user,"list:data");
        if(redisUtil.hasKey(key)){
            var cache = redisUtil.getList(key,UserNotice.class);
            if(increment){
                cache.add(0,notice);
                if(cache.size() > userNoticeCacheSize){
                    cache.remove(userNoticeCacheSize);
                }
            }
            else{
                cache.removeIf(n -> n.getId().equals(notice.getId()));
                if(getRedisUserNoticeCount(user) > cache.size()){
                    var add = userInteractionDao.findAllUserNotice(user,cache.size(),userNoticeCacheSize-cache.size());
                    cache.addAll(add);
                }
            }
            redisUtil.set(key,cache);
        }
    }

    public void setRedisUserNotice(UserNotice userNotice){
        String key = redisUtil.joinKey("user:notices",userNotice.getUser(),"list:data");
        if(redisUtil.hasKey(key)){
            var cache = redisUtil.getList(key,UserNotice.class);
            cache = cache.stream().map(n -> n.getId().equals(userNotice.getId()) ? userNotice : n).toList();
            redisUtil.set(key,cache);
        }
    }

    public void setRedisUserNoticeRead(String user){
        String key = redisUtil.joinKey("user:notices",user,"list:data");
        if (redisUtil.hasKey(key)){
            var cache = redisUtil.getList(key,UserNotice.class);
            cache.forEach(n -> n.setStatus(1));
            setRedisUserNoticeUnreadCount(user,0);
            redisUtil.set(key,cache);
        }
    }

    public void setRedisUserNoticeRead(String user,String... ids){
        String key = redisUtil.joinKey("user:notices",user,"list:data");
        if(redisUtil.hasKey(key)){
            Set<String> idSet = Set.of(ids);
            var cache = redisUtil.getList(key,UserNotice.class);
            AtomicInteger count = new AtomicInteger();
            cache.forEach(n -> {
                if(idSet.contains(n.getId()) && n.getStatus() == 0){
                    n.setStatus(1);
                    count.getAndIncrement();
                }
            });
            setRedisUserNoticeUnreadCount(user,count.get());
            redisUtil.set(key,cache);
        }
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

    public void setRedisUserNoticeCount(String user,boolean increment){
        String key = redisUtil.joinKey("user:notices",user,"list:count");
        if(redisUtil.hasKey(key)){
            redisUtil.incOrDec(increment,key);
        }
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

    public void setRedisLastUserNotice(String user,UserNotice userNotice){
        String key = redisUtil.joinKey("user:notices",user,"last");
        if(redisUtil.hasKey(key)){
            redisUtil.set(key,userNotice);
        }
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

    public void setRedisUserNoticeUnreadCount(String user,boolean increment){
        String key = redisUtil.joinKey("user:notices",user,"unread");
        if(redisUtil.hasKey(key)){
            redisUtil.incOrDec(increment,key);
        }
    }

    public void setRedisUserNoticeUnreadCount(String user,int count){
        String key = redisUtil.joinKey("user:notices",user,"unread");
        if(redisUtil.hasKey(key)){
            redisUtil.set(key,count);
        }
    }

    public FriendRelation getRedisFriendRelation(String user, String friend){
        FriendRelation friendRelation;
        String key = redisUtil.joinKey("friend:relation",user,friend);
        if(redisUtil.hasKey(key)){
            friendRelation = redisUtil.get(key, FriendRelation.class);
        }
        else{
            friendRelation = userInteractionDao.findFriendRelation(user,friend);
            redisUtil.set(key, friendRelation, 1, TimeUnit.HOURS);
        }
        return friendRelation;
    }

    public void setRedisFriendRelation(FriendRelation friendRelation){
        String key = redisUtil.joinKey("friend:relation",friendRelation.getFromUser(),friendRelation.getToUser());
        if(redisUtil.hasKey(key)){
            redisUtil.set(key, friendRelation);
        }
    }

    public void clearRedisFriendRelation(String user,String friend){
        redisUtil.deleteAsync("friend:relation",user,friend);
        redisUtil.deleteAsync("friend:relation",friend,user);
    }
}
