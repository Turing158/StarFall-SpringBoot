package com.starfall.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RedisUtil {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Async
    public void set(String key, Object value){
        set(key, value, 10 , TimeUnit.MINUTES, false);
    }

    @Async
    public void set(String key, Object value,boolean originalObj){
        var hasKey = hasKey(key);
        long ttl = 0;
        if(hasKey){
            ttl = getExpire(key);
        }
        if(ttl > 0){
            set(key, value, ttl, TimeUnit.MILLISECONDS, originalObj);
        }
        else{
            set(key, value, 10 , TimeUnit.MINUTES, originalObj);
        }
    }

    @Async
    public void set(String key, Object value, long time){
        set(key, value, time, TimeUnit.SECONDS, false);
    }

    @Async
    public void set(String key, Object value, long time,boolean originalObj){
        set(key, value, time, TimeUnit.SECONDS, originalObj);
    }

    @Async
    public void set(String key, Object value, long time, TimeUnit timeUnit) {
        set(key, value, time, timeUnit, false);
    }

    @Async
    public void set(String key, Object value, long time, TimeUnit timeUnit,boolean originalObj){
//        log.info("【设置缓存】key: {}, value: {}, time: {}, timeUnit: {}, originalObj: {}", key, value, time, timeUnit, originalObj);
        Random random = new Random();
        if(timeUnit == TimeUnit.MILLISECONDS){
            time += random.nextInt(1000);
        }
        else{
            switch (timeUnit){
                case SECONDS:
                    time = TimeUnit.SECONDS.toMillis(time);
                    break;
                case MINUTES:
                    time = TimeUnit.MINUTES.toMillis(time);
                    break;
                case HOURS:
                    time = TimeUnit.HOURS.toMillis(time);
                    break;
                case DAYS:
                    time = TimeUnit.DAYS.toMillis(time);
                    break;
                default:
                    break;
            }
            time += random.nextInt(10000);
        }
        if(originalObj){
            ValueOperations<String,Object> operations = redisTemplate.opsForValue();
            operations.set(key,value,time,TimeUnit.MILLISECONDS);
        }
        else{
            ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
            operations.set(key,JsonOperate.toJson(value),time,TimeUnit.MILLISECONDS);
        }
    }


    public boolean hasKey(String key){
        boolean r = Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
//        log.info("【检查key是否存在】{} : {}",key,r);
        return r;
    }

    public <T> T get(String key, Class<T> valueType){
        if(hasKey(key)){
            ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
            return JsonOperate.toObject(operations.get(key), valueType);
        }
        return null;
    }

    public <T> T get(String key){
        if(hasKey(key)){
            ValueOperations<String,Object> operations = redisTemplate.opsForValue();
            return (T) operations.get(key);
        }
        return null;
    }

    public void delete(String key){
        if(hasKey(key)){
            stringRedisTemplate.delete(key);
        }
    }

    public void deleteBatch(String... keys){
        for (String key : keys){
            delete(key);
        }
    }

    public Long getExpire(String key){
        return getExpire(key, TimeUnit.MILLISECONDS);
    }

    public Long getExpire(String key, TimeUnit timeUnit){
        return redisTemplate.getExpire(key, timeUnit);
    }

    public ValueOperations opsForValue(){
        return redisTemplate.opsForValue();
    }

    public ListOperations opsForList(){
        return redisTemplate.opsForList();
    }

    public <T> List<T> paginateByPageNum(List<T> array, int pageNum, int pageSize) {
        return paginateByIndex(array, (pageNum - 1) * pageSize, pageSize);
    }
    public <T> List<T> paginateByIndex(List<T> array, int index, int pageSize) {
        if (array == null || array.size() == 0) {
            return Arrays.asList();
        }
        if (index >= array.size()) {
            return Arrays.asList();
        }
        return array.stream()
                .skip(index)
                .limit(pageSize)
                .collect(Collectors.toList());
    }
}
