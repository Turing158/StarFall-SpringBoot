package com.starfall.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Autowired
    RedisTemplate redisTemplate;

    public void set(String key, Object value){
        ValueOperations<String,Object> operations = redisTemplate.opsForValue();
        operations.set(key,value);
    }

    public void set(String key, Object value, long time){
        ValueOperations<String,Object> operations = redisTemplate.opsForValue();
        operations.set(key,value,time, TimeUnit.SECONDS);
    }


    public void set(String key, Object value, long time, TimeUnit timeUnit){
        ValueOperations<String,Object> operations = redisTemplate.opsForValue();
        operations.set(key,value,time,timeUnit);
    }


    public boolean hasKey(String key){
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public Object get(String key){
        if(hasKey(key)){
            return null;
        }
        ValueOperations<String,Object> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }
}
