package com.starfall.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RedisUtil {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Async
    public void set(String key, Object value){
        var hasKey = hasKey(key);
        long ttl = 0;
        if(hasKey){
            ttl = getExpire(key);
        }
        if(ttl > 0){
            set(key, value, ttl, TimeUnit.MILLISECONDS);
        }
        else{
            set(key, value, 10 , TimeUnit.MINUTES);
        }
    }

    @Async
    public void set(String key, Object value, long time){
        set(key, value, time, TimeUnit.SECONDS);
    }

    @Async
    public void set(String key, Object value, long time, TimeUnit timeUnit){
//        log.info("【设置缓存】key: {}, value: {}, time: {}, timeUnit: {}, originalObj: {}", key, value, time, timeUnit, originalObj);
        Random random = new Random();
        if(timeUnit == TimeUnit.MILLISECONDS){
            time += random.nextInt(1000);
        }
        else{
            switch (timeUnit) {
                case SECONDS -> time = TimeUnit.SECONDS.toMillis(time);
                case MINUTES -> time = TimeUnit.MINUTES.toMillis(time);
                case HOURS -> time = TimeUnit.HOURS.toMillis(time);
                case DAYS -> time = TimeUnit.DAYS.toMillis(time);
                default -> {
                }
            }
            time += random.nextInt(10000);
        }
        ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
        operations.set(key,JsonOperate.toJson(value),time,TimeUnit.MILLISECONDS);
    }


    public boolean hasKey(String... key){
//        log.info("【检查key是否存在】{} : {}",key,r);
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(joinKey(key)));
    }

    public boolean hasKey(String key){
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    public  <T> T get(Class<T> valueType,String... keyParam){
        return get(joinKey(keyParam), valueType);
    }

    public <T> T get(String key, Class<T> valueType){
        if(hasKey(key)){
            ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
            return JsonOperate.toObject(operations.get(key), valueType);
        }
        return null;
    }

    public <T> List<T> getList(Class<T> valueType, String... keyParam){
        return getList(joinKey(keyParam), valueType);
    }

    public <T> List<T> getList(String key, Class<T> valueType){
        if(hasKey(key)){
            ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
            return JsonOperate.toList(operations.get(key), valueType);
        }
        return List.of();
    }

    public void delete(String key){
        if(hasKey(key)){
            stringRedisTemplate.delete(key);
        }
    }

    public void delete(String... keyParam){
        if(hasKey(keyParam)){
            stringRedisTemplate.delete(joinKey(keyParam));
        }
    }

    @Async
    public void deleteAsync(String key){
        if(hasKey(key)){
            stringRedisTemplate.delete(key);
        }
    }

    @Async
    public void deleteAsync(String... keyParam){
        if(hasKey(keyParam)){
            stringRedisTemplate.delete(joinKey(keyParam));
        }
    }

    public void deleteBatch(String... keys){
        stringRedisTemplate.delete(Arrays.stream(keys).toList());
    }

    public void deleteBatch(String formatKey){
        Set<String> keys = stringRedisTemplate.keys(formatKey);
        if (keys != null) {
            stringRedisTemplate.delete(keys);
        }
    }

    @Async
    public void deleteBatchAsync(String formatKey){
        Set<String> keys = stringRedisTemplate.keys(formatKey);
        if (keys != null) {
            stringRedisTemplate.delete(keys);
        }
    }

    public Long getExpire(String key){
        return getExpire(key, TimeUnit.MILLISECONDS);
    }

    public Long getExpire(String key, TimeUnit timeUnit){
        return stringRedisTemplate.getExpire(key, timeUnit);
    }

    public ValueOperations<String,String> opsForValue(){
        return stringRedisTemplate.opsForValue();
    }

    public void increment(String... keyParam){
        opsForValue().increment(joinKey(keyParam));
    }

    public void increment(long delta, String... keyParam){
        opsForValue().increment(joinKey(keyParam), delta);
    }

    public void decrement(String... keyParam){
        opsForValue().decrement(joinKey(keyParam));
    }

    public void decrement(long delta, String... keyParam){
        opsForValue().decrement(joinKey(keyParam), delta);
    }

    public void incOrDec(boolean increment, String... keyParam){
        if(increment){
            increment(keyParam);
        }
        else{
            decrement(keyParam);
        }
    }

    public void incOrDec(long delta, String... keyParam){
        if(delta > 0){
            increment(delta, keyParam);
        }
        else if(delta < 0){
            decrement(-delta, keyParam);
        }
    }

    public <T> List<T> paginateByPageNum(List<T> array, int pageNum, int pageSize) {
        return paginateByIndex(array, (pageNum - 1) * pageSize, pageSize);
    }

    public <T> List<T> paginateByIndex(List<T> array, int index, int pageSize) {
        if (array == null || array.isEmpty()) {
            return List.of();
        }
        if (index >= array.size()) {
            return List.of();
        }
        return array.stream()
                .skip(index)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

//    自动拼接带有包的key
    public String joinKey(String... keyParams){
        return String.join(":", keyParams);
    }
}
