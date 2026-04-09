package com.starfall.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starfall.Exception.ServiceException;
import com.starfall.dao.MedalDao;
import com.starfall.dao.UserDao;
import com.starfall.dao.redis.MedalRedis;
import com.starfall.entity.*;
import com.starfall.util.DateUtil;
import com.starfall.util.JsonOperate;
import com.starfall.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j

public class MedalService {
    @Autowired
    MedalDao medalDao;
    @Autowired
    UserDao userDao;
    @Autowired
    MedalRedis medalRedis;
    @Autowired
    WebSocketService webSocketService;
    @Autowired
    UserNoticeService userNoticeService;
    @Autowired
    DateUtil dateUtil;
    @Autowired
    JwtUtil jwtUtil;

    public List<MedalMapper> findUserMedalOnMenu(String token){
        String user = jwtUtil.getTokenField(token,"USER");
        return medalRedis.getMedalOnPosition(user,true);
    }

    public List<MedalMapper> findUserMedal(String user){
        return medalRedis.getMedalOnPosition(user,false);
    }

    public List<MedalMapper> findAllMedal(String user,int page){
        return medalRedis.getUserMedalAll(user,page);
    }

    public Medal findMedalById(String id){
        return medalRedis.getMedalById(id);
    }

    @Async
    @Transactional
    public void gainRegisterMedal(String user){
        String id = "m202212011022334567a4c8";
        if(isExistMedal(user,id)){
            return;
        }
        var mapper = getMedalMapper(id,user);
        var ldt = LocalDateTime.now().plusDays(7);
        mapper.setExpireTime(dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss"));
        insertMedalAndNotice(mapper);
    }

    @Async
    @Transactional
    public void checkAndGainSignInCount30Or365DayMedal(int count,boolean is30Day,String user){
        if(count < (is30Day ? 30 : 365)){
            return;
        }
        String id = is30Day ? "m202212111546378901q5w3" : "m202212180932154321t8u1";
        if(isExistMedal(user,id)){
            return;
        }
        var mapper = getMedalMapper(id,user);
        insertMedalAndNotice(mapper);
    }

    @Async
    @Transactional
    public void checkAndGainRegisterAlready3year(String user){
        log.info("checkAndGainRegisterAlready3year user:{}",user);
        String id = "m202212051433221234x9y2";
        if(isExistMedal(user,id)){
            log.info("checkAndGainRegisterAlready3year user:{} 已获得3年注册勋章",user);
            return;
        }
        if(medalDao.countUserAlready3Year(user) == 0){
            return;
        }
        var mapper = getMedalMapper(id,user);
        insertMedalAndNotice(mapper);
    }

    @Async
    @Transactional
    public void checkAndGainMinecraftPlayer(String response,String user){
        log.info("checkAndGainMinecraftPlayer");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            log.info("checkAndGainMinecraftPlayer error:{}",e.getMessage());
        }
        String nodeId = node.get("id").asText();
        String nodeName = node.get("name").asText();
        userDao.updateOnlineName(user,nodeName,dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
        String uuid = userDao.findOnlineUuidByUser(user);
        String userByUuid = userDao.findUserByOnlineUUID(nodeId);
        if(!(userByUuid == null || uuid == null || uuid.equals(nodeId))){
            log.info("checkAndGainMinecraftPlayer user:{} 已绑定其他玩家，无法获得正版玩家勋章",user);
            var noticeAction = new UserTempNotice("无法获取正版玩家勋章","当前账号的UUID已绑定其他用户，无法获得正版玩家勋章，请联系管理员处理!",dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
            webSocketService.sendMessageToUser(user, JsonOperate.toJson(noticeAction,false));
            return;
        }
        if(uuid == null || uuid.isEmpty()){
            userDao.updateOnlineUuid(user,nodeId,dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
        }
        String id = "m202212251234567890o6p2";
        if(isExistMedal(user,id)){
            log.info("checkAndGainMinecraftPlayer user:{} 已获得正版玩家勋章",user);
            return;
        }
        var mapper = getMedalMapper(id,user);
        insertMedalAndNotice(mapper);
    }

    @Async
    @Transactional
    public void gainMedal(String id,String user,String expireTime){
        var existMedal = isExistMedal(user,id);
        var mapper = getMedalMapper(id,user);
        if(existMedal && mapper.getExpireTime() == null && expireTime == null){
            throw new ServiceException("MEDAL_EXIST","该勋章已获得，无需重复获得");
        }
        mapper.setExpireTime(expireTime);
        insertMedalAndNotice(mapper,!existMedal);
    }

    //辅助方法

    @Async
    @Transactional
    public void insertMedalNotice(MedalMapper medalMapper){
        MedalNoticeAction medalNoticeAction =
                new MedalNoticeAction(
                        medalMapper.getId(),
                        medalMapper.getName(),
                        medalMapper.getIcon(),
                        medalMapper.getDescription(),
                        medalMapper.getGainTime(),
                        medalMapper.getExpireTime()
                );
        log.info("insertMedalNotice MedalNoticeAction：{}",medalNoticeAction);
        userNoticeService.insertNotice(
                medalMapper.getUser(),
                UserNoticeType.msg,
                "恭喜获得 " + medalMapper.getName(),
                JsonOperate.toJson(medalNoticeAction,false)
        );
    }

    private boolean isExistMedal(String user, String medalId){
        return medalDao.countByUserAndMedal(user, medalId) >= 1;
    }

    private MedalMapper getMedalMapper(String id,String user){
        Medal medal = medalRedis.getMedalById(id);
        MedalMapper medalMapper = new MedalMapper();
        medalMapper.setId(id);
        medalMapper.setName(medal.getName());
        medalMapper.setIcon(medal.getIcon());
        medalMapper.setDescription(medal.getDescription());
        medalMapper.setUser(user);
        medalMapper.setGainTime(dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
        log.info("getMedalMapper medalMapper：id:{},name:{},icon:{},descirppp:{},source:{}",medalMapper.getId(),medalMapper.getName(),medalMapper.getIcon(),medalMapper.getDescription(),medalMapper.getSource());
        return medalMapper;
    }

    public void insertMedalAndNotice(MedalMapper medalMapper){
        insertMedalAndNotice(medalMapper,true);
    }

    @Transactional
    public void insertMedalAndNotice(MedalMapper medalMapper,boolean isNewGain){
        medalRedis.clearUserMedalCache(medalMapper.getUser());
        if(isNewGain){
            medalDao.insertMedalMapper(medalMapper);
        }
        else{
            medalDao.updateMedalMapperGainTimeAndExpireTime(medalMapper);
        }
        log.info("insertMedalAndNotice medalMapper：id:{},name:{},icon:{},descirppp:{},source:{}",medalMapper.getId(),medalMapper.getName(),medalMapper.getIcon(),medalMapper.getDescription(),medalMapper.getSource());
        insertMedalNotice(medalMapper);
    }
}
