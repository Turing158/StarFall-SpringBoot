package com.starfall.dao.redis;

import com.starfall.dao.SignInDao;
import com.starfall.dao.UserDao;
import com.starfall.entity.SignIn;
import com.starfall.entity.User;
import com.starfall.entity.UserPersonalized;
import com.starfall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class UserRedis {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserDao userDao;
    @Autowired
    private SignInDao signInDao;

    private int signInCachePage = 4;
    private int signInPageSize = 6;


    public UserPersonalized findRedisUserPersonalized(String user){
        UserPersonalized personalized;
        if(redisUtil.hasKey("user:personalized", user)){
            personalized = redisUtil.get(redisUtil.joinKey("user:personalized", user), UserPersonalized.class);
        }
        else{
            personalized = userDao.findPersonalizedByUser(user);
            redisUtil.set(redisUtil.joinKey("user:personalized", user), personalized);
        }
        return personalized;
    }

    public void setRedisUserPersonalized(String user, UserPersonalized personalized){
        if(redisUtil.hasKey("user:personalized", user)){
            redisUtil.set(redisUtil.joinKey("user:personalized", user), personalized);
        }
    }

    public User findRedisUser(String user){
        User userObj;
        if(redisUtil.hasKey("user:cache", user)){
            userObj = redisUtil.get(redisUtil.joinKey("user:cache", user), User.class);
        }
        else{
            userObj = userDao.findByUserOrEmail(user);
            redisUtil.set(redisUtil.joinKey("user:cache", user), userObj, 30 , TimeUnit.MINUTES);
        }
        return userObj;
    }

    public void setRedisUser(String user, User userObj){
        if(redisUtil.hasKey("user:cache", user)){
            redisUtil.set(redisUtil.joinKey("user:cache", user), userObj);
        }
    }

    public boolean existRedisUser(String user){
        if(redisUtil.hasKey("user:cache", user)){
            return true;
        }
        return userDao.existUser(user) >= 1;
    }

    public List<SignIn> findRedisSignIn(String user,int page){
        List<SignIn> signIns;
        if(page <= signInCachePage){
            if(redisUtil.hasKey("user:signIn",user,"data")){
                var cache = redisUtil.getList(SignIn.class,"user:signIn",user,"data");
                signIns = redisUtil.paginateByPageNum(cache, page, signInPageSize);
            }
            else{
                var cache = signInDao.findAllSignInByUser(user, 0,signInCachePage*signInPageSize);
                signIns = redisUtil.paginateByPageNum(cache, page, signInPageSize);
                redisUtil.set(redisUtil.joinKey("user:signIn",user,"data"), cache, 1 , TimeUnit.DAYS);
            }
        }
        else{
            signIns = signInDao.findAllSignInByUser(user, (page-1)*signInPageSize,signInPageSize);
        }
        return signIns;
    }

     public void setRedisSignIn(String user, SignIn signIns){
        if(redisUtil.hasKey("user:signIn",user,"data")){
            var cache = redisUtil.getList(SignIn.class,"user:signIn",user,"data");
            cache.add(0,signIns);
            if(cache.size() > signInCachePage*signInPageSize){
                cache.remove(cache.size()-1);
            }
            redisUtil.set(redisUtil.joinKey("user:signIn",user,"data"), cache);
        }
     }

     public int findRedisSignInCount(String user){
        int count = 0;
        if(redisUtil.hasKey("user:signIn",user,"count")){
            count = redisUtil.get(Integer.class,"user:signIn",user,"count");
        }
        else{
            count = signInDao.countSignInByUser(user);
            redisUtil.set(redisUtil.joinKey("user:signIn",user,"count"), count, 1 , TimeUnit.DAYS);
        }
        return count;
     }

     public void setRedisSignInCount(String user,boolean increment){
        if(redisUtil.hasKey("user:signIn",user,"count")){
            if(increment){
                redisUtil.increment("user:signIn",user,"count");
            }
            else{
                redisUtil.decrement("user:signIn",user,"count");
            }
        }
     }

     public int findRedisSignInContinualCount(String user){
        int count = 0;
        if(redisUtil.hasKey("user:signIn",user,"continual")){
            count = redisUtil.get(Integer.class,"user:signIn",user,"continual");
        }
        else{
            count = signInDao.countContinualSignIn(user);
            redisUtil.set(redisUtil.joinKey("user:signIn",user,"continual"), count, 1 , TimeUnit.DAYS);
        }
        return count;
     }

     public void setRedisSignInContinualCount(String user,boolean isContinual){
        if(redisUtil.hasKey("user:signIn",user,"continual")){
            if(isContinual){
                redisUtil.increment("user:signIn",user,"continual");
            }
            else{
                redisUtil.set(redisUtil.joinKey("user:signIn",user,"continual"), 1);
            }
        }
     }
}
