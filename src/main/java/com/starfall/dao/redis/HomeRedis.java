package com.starfall.dao.redis;

import com.starfall.dao.HomeDao;
import com.starfall.entity.Advertisement;
import com.starfall.entity.HomeTalk;
import com.starfall.entity.Notice;
import com.starfall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class HomeRedis {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    HomeDao homeDao;

    final int homeTalkPageSize = 20;
    final int homeTalkCacheSize = 80;

    public List<HomeTalk> getRedisHomeTalks(int num){
        List<HomeTalk> homeTalks;
        String key = redisUtil.joinKey("homeTalk:cache:data");
        if(num <= homeTalkCacheSize){
            if(redisUtil.hasKey(key)){
                var cache = redisUtil.getList(key, HomeTalk.class);
                homeTalks = redisUtil.paginateByIndex(cache, num, homeTalkPageSize);
            }
            else{
                var cache = homeDao.findAllHomeTalk(0,homeTalkCacheSize);
                homeTalks = redisUtil.paginateByIndex(cache, num, homeTalkPageSize);
                redisUtil.set(key, cache, 1, TimeUnit.HOURS);
            }
        }
        else{
            homeTalks = homeDao.findAllHomeTalk(num,homeTalkPageSize);
        }
        return homeTalks;
    }

    public void setRedisHomeTalks(HomeTalk talk,boolean increment){
        String key = redisUtil.joinKey("homeTalk:cache:data");
        if(redisUtil.hasKey(key)){
            var cache = redisUtil.getList(key, HomeTalk.class);
            if(increment){
                cache.add(0,talk);
                if(cache.size() > homeTalkCacheSize){
                    cache.remove(homeTalkCacheSize);
                }
            }
            else{
                var isDel = cache.removeIf(t -> t.getId().equals(talk.getId()));
                if(isDel && cache.size() < homeTalkCacheSize){
                    var newTalk = homeDao.findAllHomeTalk(cache.size(),homeTalkCacheSize-cache.size());
                    cache.addAll(cache.size(),newTalk);
                }
            }
            redisUtil.set(key, cache);
        }
    }

    public int getRedisHomeTalkCount(){
        int count;
        String key = redisUtil.joinKey("homeTalk:cache:count");
        if(redisUtil.hasKey(key)){
            count = redisUtil.get(key, Integer.class);
        }
        else {
            count = homeDao.countHomeTalk();
            redisUtil.set(key, count, 1, TimeUnit.HOURS);
        }
        return count;
    }

    public void setRedisHomeTalkCount(boolean increment){
        String key = redisUtil.joinKey("homeTalk:cache:count");
        if(redisUtil.hasKey(key)){
            redisUtil.incOrDec(increment, key);
        }
    }

    public HomeTalk getRedisHomeTalk(String id){
        String key = redisUtil.joinKey("homeTalk:cache:single",id);
        HomeTalk talk;
        if(redisUtil.hasKey(key)){
            talk = redisUtil.get(key, HomeTalk.class);
        }
        else{
            String cacheDataKey = redisUtil.joinKey("homeTalk:cache:data");
            if(redisUtil.hasKey(cacheDataKey)){
                var cache = redisUtil.getList(cacheDataKey, HomeTalk.class);
                var talkCacheData = cache.stream().filter(t -> t.getId().equals(id)).findFirst();
                talk = talkCacheData.orElseGet(() -> homeDao.findHomeTalk(id));
            }
            else{
                talk = homeDao.findHomeTalk(id);
            }
            redisUtil.set(key, talk, 1, TimeUnit.HOURS);
        }
        return talk;
    }

    public void setRedisHomeTalk(HomeTalk talk,boolean increment){
        String key = redisUtil.joinKey("homeTalk:cache:single",talk.getId());
        if(redisUtil.hasKey(key)){
            if(increment){
                redisUtil.set(key, talk);
            }
            else{
                redisUtil.delete(key);
            }
        }
    }

    public HomeTalk getRedisHomeTalkMapper(String user,String date){
        HomeTalk talk;
        String key = redisUtil.joinKey("homeTalk:user",user,date.split(" ")[0]);
        if(redisUtil.hasKey(key)){
            talk = redisUtil.get(key,HomeTalk.class);
        }
        else{
            talk = homeDao.findHomeTalkByUserAndDate(user);
            redisUtil.set(key, talk, 1, TimeUnit.HOURS);
        }
        return talk;
    }

    public void setRedisHomeTalkMapper(HomeTalk talk,boolean increment){
        String key = redisUtil.joinKey("homeTalk:user",talk.getUser(), talk.getDate().split(" ")[0]);
        if(redisUtil.hasKey(key)){
            if(increment){
                redisUtil.set(key, talk);
            }
            else{
                redisUtil.delete(key);
            }
        }
    }

    public List<Advertisement> getRedisAdvertisementByPosition(String position){
        List<Advertisement> advertisements;
        String key = redisUtil.joinKey("advertisements:position",position);
        if(redisUtil.hasKey(key)){
             advertisements = redisUtil.getList(key, Advertisement.class);
        }
        else{
            advertisements = homeDao.findAdvertisementByPosition(position);
            redisUtil.set(key,advertisements, 1, TimeUnit.DAYS);
        }
        return advertisements;
    }

    public List<Notice> getRedisNotice(){
        List<Notice> notices;
        String key = redisUtil.joinKey("notices:cache");
        if(redisUtil.hasKey(key)){
            notices = redisUtil.getList(key, Notice.class);
        }
        else{
            notices = homeDao.findAllNotice();
            redisUtil.set(key, notices, 1, TimeUnit.HOURS);
        }
        return notices;
    }
}
