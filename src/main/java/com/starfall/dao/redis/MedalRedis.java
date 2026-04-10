package com.starfall.dao.redis;

import com.starfall.dao.MedalDao;
import com.starfall.entity.Medal;
import com.starfall.entity.MedalMapper;
import com.starfall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class MedalRedis {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    MedalDao medalDao;

    final int menuMedalSize = 3;
    final int personalmedalSize = 11;

    final int userMedalCachePage = 2;
    final int userMedalPageSize = 20;

    // 缓存菜单和个人信息显示勋章
    public List<MedalMapper> getMedalOnPosition(String user,boolean isMenu){
        List<MedalMapper> medalMappers;
        String key = redisUtil.joinKey("medal:user",user,isMenu ? "menu" : "personal");
        boolean needDatasource;
        if(redisUtil.hasKey(key)){
            var cache = redisUtil.getList(key, MedalMapper.class);
            LocalDateTime ldt = LocalDateTime.now();
            needDatasource = cache.stream().anyMatch(m -> m.getExpireTime() != null && !m.getExpireTime().isEmpty() && LocalDateTime.parse(m.getExpireTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).isBefore(ldt));
            if(!needDatasource){
                medalMappers = cache;
            }
            else{
                medalMappers = medalDao.findAllByUserLimit(user, 0, isMenu ? menuMedalSize : personalmedalSize);
                redisUtil.set(key, medalMappers, 10, TimeUnit.MINUTES);
            }
        }
        else{
            medalMappers = medalDao.findAllByUserLimit(user, 0,  isMenu ? menuMedalSize : personalmedalSize);
            redisUtil.set(key, medalMappers, 10, TimeUnit.MINUTES);
        }
        return medalMappers;
    }

    public List<MedalMapper> getUserMedalAll(String user,int page){
        List<MedalMapper> medalMappers;
        String key = redisUtil.joinKey("medal:user",user,"all");
        if(page <= userMedalCachePage){
            boolean needDatasource;
            if(redisUtil.hasKey(key)){
                var cache = redisUtil.getList(key, MedalMapper.class);
                LocalDateTime ldt = LocalDateTime.now();
                needDatasource = cache.stream().anyMatch(m -> m.getExpireTime() != null && !m.getExpireTime().isEmpty() && LocalDateTime.parse(m.getExpireTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).isBefore(ldt));
                if(!needDatasource){
                    medalMappers = redisUtil.paginateByPageNum(cache, page, userMedalPageSize);
                }
                else{
                    var cacheDatasource = medalDao.findAllMedal(user, 0,userMedalCachePage * userMedalPageSize);
                    medalMappers = redisUtil.paginateByPageNum(cacheDatasource, page, userMedalPageSize);
                    redisUtil.set(key, cacheDatasource, 10, TimeUnit.MINUTES);
                }
            }
            else{
                var cacheDatasource = medalDao.findAllMedal(user, 0,userMedalCachePage * userMedalPageSize);
                medalMappers = redisUtil.paginateByPageNum(cacheDatasource, page, userMedalPageSize);
                redisUtil.set(key, cacheDatasource, 10, TimeUnit.MINUTES);
            }
        }
        else{
            medalMappers = medalDao.findAllMedal(user, (page-1)*20, userMedalPageSize);
        }
        return medalMappers;
    }

    public Medal getMedalById(String id){
        Medal medal;
        String key = redisUtil.joinKey("medal:cache",id);
        if(redisUtil.hasKey(key)){
            medal = redisUtil.get(key, Medal.class);
        }
        else{
            medal = medalDao.findById(id);
            redisUtil.set(key, medal, 1, TimeUnit.HOURS);
        }
        return medal;
    }

    @Async
    public void clearUserMedalCache(String user){
        redisUtil.deleteBatch("medal:user:"+user+":*");
    }
}
