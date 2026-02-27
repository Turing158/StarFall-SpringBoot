package com.starfall.service;

import com.starfall.dao.HomeDao;
import com.starfall.dao.UserDao;
import com.starfall.entity.Advertisement;
import com.starfall.entity.HomeTalk;
import com.starfall.entity.LiveBroadcast;
import com.starfall.entity.ResultMsg;
import com.starfall.util.CodeUtil;
import com.starfall.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class HomeService {
    @Autowired
    private HomeDao homeDao;
    @Autowired
    private UserDao userDao;

    public ResultMsg findAllHomeTalk(int num) {
        return ResultMsg.success(homeDao.findAllHomeTalk(num));
    }

    public ResultMsg publicHomeTalk(String content,String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        LocalDateTime ldt = LocalDateTime.now();
        HomeTalk talk = homeDao.findHomeTalk(user);
        if(talk != null){
            LocalDateTime oldLdt = LocalDateTime.parse(talk.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if(oldLdt.getDayOfWeek() == ldt.getDayOfWeek()){
                return ResultMsg.error("REPEATED");
            }
        }
        int status = homeDao.insertHomeTalk(new HomeTalk(user,content,ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        if(status == 1){
            return ResultMsg.success();
        }
        return ResultMsg.error("ERROR");
    }

    public ResultMsg deleteHomeTalk(String date,String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        HomeTalk talk = homeDao.findHomeTalkByUserAndDate(user,date);
        if (talk != null){
            int status = homeDao.deleteHomeTalk(user,date);
            return status == 1 ? ResultMsg.success() : ResultMsg.error("ERROR");
        }
        return ResultMsg.error("NOT_EXIST");
    }

    public ResultMsg findAdvertisementByPosition(String position){
        return ResultMsg.success(homeDao.findAdvertisementByPosition(position));
    }
}
