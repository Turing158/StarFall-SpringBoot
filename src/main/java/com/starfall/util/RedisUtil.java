package com.starfall.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
//    @Autowired
//    RedisTemplate redisTemplate;
    @Autowired
    StringRedisTemplate stringRedisTemplate;



    public void set(String key, Object value){
        ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
        Long ttl = stringRedisTemplate.getExpire(key);
        String saveValue = JsonOperate.toJson(value);
        if (ttl > 0) {
            operations.set(key, saveValue, ttl, TimeUnit.SECONDS);
        } else {
            operations.set(key, saveValue, 3, TimeUnit.MINUTES);
        }
    }

    public void set(String key, Object value, long time){
        ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
        operations.set(key,JsonOperate.toJson(value),time, TimeUnit.SECONDS);
    }


    public void set(String key, Object value, long time, TimeUnit timeUnit){
        ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
        operations.set(key,JsonOperate.toJson(value),time,timeUnit);
    }


    public boolean hasKey(String key){
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    public <T> T get(String key, Class<T> valueType){
        if(hasKey(key)){
            ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
            return JsonOperate.toObject(operations.get(key), valueType);
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
        return getExpire(key, TimeUnit.SECONDS);
    }

    public Long getExpire(String key, TimeUnit timeUnit){
        return stringRedisTemplate.getExpire(key, timeUnit);
    }
}
