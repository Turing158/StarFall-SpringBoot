package com.starfall.dao.redis;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.starfall.entity.LiveBroadcastHistory;
import com.starfall.entity.LiveBroadcastShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.starfall.dao.LiveDao;
import com.starfall.util.RedisUtil;

@Component
public class LiveRedis {

    @Autowired
    private LiveDao liveDao;
    @Autowired
    private RedisUtil redisUtil;

    final int livePageSize = 10;
    final int liveCacheSize = 40;
    final int liveHistoryPageSize = 10;
    final int liveHistoryCachePage = 4;

    public List<LiveBroadcastShow> getRedisLiveShow(int index){
        String key = redisUtil.joinKey("live:home:cache:data");
        List<LiveBroadcastShow> liveBroadcastShows;
        if(index < liveCacheSize){
            if(redisUtil.hasKey(key)){
                var cache = redisUtil.getList(key, LiveBroadcastShow.class);
                liveBroadcastShows = redisUtil.paginateByIndex(cache, index, livePageSize);
            }
            else{
                var cache = liveDao.findAllLiveShow(0, liveCacheSize);
                redisUtil.set(key, cache, 1, TimeUnit.HOURS);
                liveBroadcastShows = redisUtil.paginateByIndex(cache, index, livePageSize);
            }
        }
        else{
            liveBroadcastShows = liveDao.findAllLiveShow(index, livePageSize);
        }
        return liveBroadcastShows;
    }

    public void clearRedisLiveShow(){
        redisUtil.deleteAsync("live:home:cache:data");
    }

    public List<LiveBroadcastHistory> getRedisLiveHistory(String user, int page){
        String key = redisUtil.joinKey("live:history",user,"cache:data");
        List<LiveBroadcastHistory> liveBroadcastHistories;
        if(page <= liveHistoryCachePage){
            if(redisUtil.hasKey(key)){
                var cache = redisUtil.getList(key, LiveBroadcastHistory.class);
                liveBroadcastHistories = redisUtil.paginateByIndex(cache, (page-1)*liveHistoryPageSize, liveHistoryPageSize);
            }
            else{
                var cache = liveDao.findAllLiveByUser(user,0, liveHistoryPageSize * liveHistoryCachePage);
                redisUtil.set(key, cache, 1, TimeUnit.HOURS);
                liveBroadcastHistories = redisUtil.paginateByIndex(cache, (page-1)*liveHistoryPageSize, liveHistoryPageSize);
            }
        }
        else{
            liveBroadcastHistories = liveDao.findAllLiveByUser(user,(page-1)*liveHistoryPageSize, liveHistoryPageSize);
        }
        return liveBroadcastHistories;
    }
    
    public void clearRedisLiveHistory(String user){
        redisUtil.deleteAsync("live:history",user,"cache:data");
    }

    public int getRedisLiveHistoryCount(String user){
        String key = redisUtil.joinKey("live:history",user,"cache:count");
        if(redisUtil.hasKey(key)){
            return redisUtil.get(key,Integer.class);
        }
        else{
            return liveDao.countLiveByUser(user);
        }
    }

    public void setRedisLiveHistoryCount(String user,boolean increment){
        String key = redisUtil.joinKey("live:history",user,"cache:count");
        if(redisUtil.hasKey(key)){
            redisUtil.incOrDec(increment,key);
        }
    }
}