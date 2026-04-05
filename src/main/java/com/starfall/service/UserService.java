package com.starfall.service;

import cn.hutool.dfa.SensitiveUtil;
import com.starfall.Exception.ServiceException;
import com.starfall.dao.SignInDao;
import com.starfall.dao.UserDao;
import com.starfall.dao.redis.UserRedis;
import com.starfall.entity.*;
import com.starfall.util.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserService {
    @Autowired
    FileService fileService;
    @Autowired
    SearchService searchService;
    @Autowired
    MedalService medalService;
    @Autowired
    UserDao userDao;
    @Autowired
    SignInDao signInDao;
    @Autowired
    UserRedis userRedis;
    @Autowired
    EncDecUtil encDecUtil;
    @Autowired
    MailUtil mailUtil;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CodeUtil codeUtil;
    @Autowired
    DateUtil dateUtil;
    @Autowired
    JwtUtil jwtUtil;

    public String login(String account, String password,String code) {
        if(!codeUtil.checkCode(code)){
            throw new ServiceException("CODE_ERROR", "验证码错误");
        }
        boolean flag = account.contains("@");
        if(flag){
            if(userDao.existEmail(account) != 1){
                throw new ServiceException("EMAIL_ERROR", "邮箱不存在");
            }
            return loginSuccess(account, password);
        }
        if(userDao.existUser(account) != 1){
            throw new ServiceException("USER_ERROR", "用户不存在");

        }
        return loginSuccess(account, password);
    }

    private String loginSuccess(String account, String password){
        User user = userDao.findByUserOrEmail(account);
        if(!user.getPassword().equals(encDecUtil.aesEncrypt(password))){
            throw new ServiceException("PASSWORD_ERROR", "密码错误");
        }
        Map<String,Object> claims = new HashMap<>();
        claims.put("USER",user.getUser());
        claims.put("EMAIL",user.getEmail());
        claims.put("ROLE",user.getRole());
        String token = jwtUtil.generateToken(claims);
        medalService.checkAndGainRegisterAlready3year(user.getUser());
        redisUtil.set("onlineUser:" + token,user.toUserVO(),10, TimeUnit.MINUTES);
        return token;
    }

    public ResultMsg getUserInfo(String token){
        String user = jwtUtil.getTokenField(token,"USER");
        token = jwtUtil.parseTokenStr(token);
        UserVO userVO = null;
        if(redisUtil.hasKey("onlineUser:" + token)){
            userVO = redisUtil.get("onlineUser:" + token, UserVO.class);
        }
        else{
            userVO = userRedis.findRedisUser(user).toUserVO();
            redisUtil.set("onlineUser:" + token,userVO,10 ,TimeUnit.MINUTES);
        }
        return ResultMsg.success(userVO);
    }

    @Transactional
    public void register(String user, String password, String email,String emailCode,String code){
        if(!codeUtil.checkCode(code)){
            throw new ServiceException("CODE_ERROR", "验证码错误");
        }
        if(userDao.existUser(user) != 0){
            throw new ServiceException("USER_ERROR", "用户名已存在");
        }
        if(userDao.existEmail(email) != 0){
            throw new ServiceException("EMAIL_ERROR", "邮箱已存在");
        }
        if(!codeUtil.checkEmailCode("regEmailCode:",email,emailCode)){
            throw new ServiceException("EMAIL_CODE_ERROR", "邮箱验证码错误");
        }
        LocalDateTime ldt = LocalDateTime.now();
        String date = dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd");
        String datetime = dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss");
        String name = "新用户"+dateUtil.getDateTimeByFormat(ldt,"yyyyMMdd");
        User userObj = new User(user, encDecUtil.aesEncrypt(password), name, 0,email, date, 0, 1,"default.png","user",datetime,datetime);
        UserPersonalized userPersonalized = new UserPersonalized(user,"这个人很懒，什么都没有留下~",null,1,1,1,1,0,datetime,datetime);
        medalService.gainRegisterMedal(user);
        userDao.insertUser(userObj);
        userDao.insertPersonalized(userPersonalized);
    }

    public ResultMsg getEmailCode(String email,boolean isRegister){
        if(redisUtil.getExpire((isRegister ? "regEmailCode:" : "forgetEmailCode:") + email) > 4 * 60){
            return ResultMsg.error("SEND_FAST_ERROR");
        }
        String code = CodeUtil.getCode(6);
        if(isRegister){
            mailUtil.reg_mail(email,code.toUpperCase());
        }
        else{
            mailUtil.custom_mail(email,"忘记密码",code.toUpperCase());
        }
        redisUtil.set((isRegister ? "regEmailCode:" : "forgetEmailCode:") + email,code.toLowerCase(),5, TimeUnit.MINUTES);
        return ResultMsg.success();
    }



    public ResultMsg checkForgetPassword(String email,String emailCode,String code){
        if(codeUtil.checkCode(code)){
            if(codeUtil.checkEmailCode("forgetEmailCode:",email,emailCode)){
                User user = userDao.findByUserOrEmail(email);
                if(user != null) {
                    Map<String,Object> claims = new HashMap<>();
                    claims.put("USER",user.getUser());
                    claims.put("EMAIL",user.getEmail());
                    claims.put("CODE",emailCode);
                    String token = jwtUtil.generateToken(claims, 5*60);
                    redisUtil.set("forgetPassword:" + email, token, 5, TimeUnit.MINUTES);
                    return ResultMsg.success(token);
                }
                return ResultMsg.error("EMAIL_ERROR");
            }
            return ResultMsg.error("EMAIL_CODE_ERROR");
        }
        return ResultMsg.error("CODE_ERROR");
    }

    @Transactional
    public ResultMsg forgetPassword(String token,String password){
        String user = jwtUtil.getTokenField(token,"USER");
        String email = jwtUtil.getTokenField(token,"EMAIL");
        String code = jwtUtil.getTokenField(token,"CODE");
        if(redisUtil.hasKey("forgetPassword:" + email)){
            String redisToken = redisUtil.get("forgetPassword:" + email,String.class);
            redisUtil.delete("forgetPassword:" + email);
            if(!redisToken.equals(token)){
                return ResultMsg.error("TOKEN_ERROR");
            }
        }
        if(codeUtil.checkEmailCode("forgetEmailCode:",email,code,true)){
            userDao.updatePassword(user, encDecUtil.aesEncrypt(password), dateUtil.getDateTimeByFormat(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss"));
            redisUtil.delete("user:cache:" + user);
            return ResultMsg.success();
        }
        return ResultMsg.error("EMAIL_CODE_ERROR");
    }

    @Transactional
    public UserVO settingInfo(String token,String name,int gender,String birthday,String code){
        if(!codeUtil.checkCode(code)){
            throw new ServiceException("CODE_ERROR", "验证码错误");
        }
        var sensitiveWords = SensitiveUtil.getFoundAllSensitive(name);
        if(!sensitiveWords.isEmpty()){
            throw new ServiceException("SENSITIVE_ERROR", "用户名包含敏感词");
        }
        String user = jwtUtil.getTokenField(token,"USER");
        User userObj = userRedis.findRedisUser(user);
        String date = dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss");
        int status = userDao.updateInfo(user,name,gender,birthday, date);
        userObj.setName(name);
        userObj.setGender(gender);
        userObj.setBirthday(birthday);
        userObj.setUpdateTime(date);
        userRedis.setRedisUser(user, userObj);
        if(status != 1){
            throw new ServiceException("DATASOURCE_ERROR", "数据源错误");
        }

        redisUtil.set("onlineUser:" + token,userObj.toUserVO(),30, TimeUnit.MINUTES);
        searchService.saveUserName(user,name);
        return userObj.toUserVO();
    }

    @Transactional
    public ResultMsg settingPassword(String token,String oldPassword,String newPassword,String code){
        if(codeUtil.checkCode(code)){
            String user = jwtUtil.getTokenField(token,"USER");
            User userObj = userRedis.findRedisUser(user);
            String encryptOldPassword = encDecUtil.aesEncrypt(oldPassword);
            if(userObj.getPassword().equals(encryptOldPassword)){
                String encryptNewPassword = encDecUtil.aesEncrypt(newPassword);
                String date = dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss");
                int status = userDao.updatePassword(user,encryptNewPassword, date);
                userObj.setPassword(encryptNewPassword);
                userObj.setUpdateTime(date);
                userRedis.setRedisUser(user, userObj);
                if(status == 1){
                    return ResultMsg.success();
                }
                return ResultMsg.error("DATASOURCE_ERROR");
            }
            return ResultMsg.error("PASSWORD_ERROR");
        }
        return ResultMsg.error("CODE_ERROR");
    }

    @Transactional
    public ResultMsg settingAvatar(String token,String avatarBase64){
        String user = jwtUtil.getTokenField(token,"USER");
        User userObj = userRedis.findRedisUser(user);
        String oldAvatar = userObj.getAvatar();
        String avatarName = dateUtil.getDateTimeByFormat("yyyyMMddHHmmssSSSS") + CodeUtil.getCode(6);
        String fileName = avatarName + ".png";
        String folder = "user/"+user+"/avatar";
        MultipartFile file = new MultipartFileImpl(CodeUtil.getBase64Bytes(avatarBase64),fileName);
        fileService.upload(file,folder,fileName);
        if(!oldAvatar.equals("default.png")){
            fileService.removeFile(oldAvatar);
        }
        String date = dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss");
        userDao.updateAvatar(user,folder+"/"+fileName, date);
        userObj.setAvatar(folder+"/"+fileName);
        redisUtil.set("onlineUser:" + jwtUtil.parseTokenStr(token) ,userObj.toUserVO(),5, TimeUnit.MINUTES);
        userObj.setUpdateTime(date);
        userRedis.setRedisUser(user, userObj);
        return ResultMsg.success(folder+"/"+fileName);
    }

    public ResultMsg sendOldEmailCode(String token){
        String user = jwtUtil.getTokenField(token,"USER");
        User userObj = userRedis.findRedisUser(user);
        if(redisUtil.getExpire("oldEmailCode:" + userObj.getEmail()) > 4 * 60){
            return ResultMsg.error("SEND_FAST_ERROR");
        }
        String code = CodeUtil.getCode(6);
        redisUtil.set("oldEmailCode:" + userObj.getEmail(),code.toLowerCase(),5, TimeUnit.MINUTES);
        mailUtil.custom_mail(userObj.getEmail(),"修改旧邮箱",code.toUpperCase());
        return ResultMsg.success();
    }


    public ResultMsg sendNewEmailCode(String token,String email){
        String user = jwtUtil.getTokenField(token,"USER");
        if(redisUtil.getExpire("oldEmailCode:" + email) > 4 * 60){
            return ResultMsg.error("SEND_FAST_ERROR");
        }
        String code = CodeUtil.getCode(6);
        redisUtil.set("newEmailCode:" + email,code.toLowerCase(),5, TimeUnit.MINUTES);
        mailUtil.custom_mail(email,"新邮箱",code.toUpperCase());
        return ResultMsg.success();
    }

    @Transactional
    public ResultMsg settingEmail(String token,String newEmail,String oldEmailCode,String newEmailCode){
        String user = jwtUtil.getTokenField(token,"USER");
        User userObj = userRedis.findRedisUser(user);
        if(codeUtil.checkEmailCode("oldEmailCode:",userObj.getEmail(),oldEmailCode)){
            if(codeUtil.checkEmailCode("newEmailCode:",newEmail,newEmailCode)){
                if(userDao.existEmail(newEmail) == 0){
                    String date = dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss");
                    int status = userDao.updateEmail(user,newEmail, date);
                    userObj.setEmail(newEmail);
                    userObj.setUpdateTime(date);
                    userRedis.setRedisUser(user, userObj);
                    if(status == 1){
                        UserVO userVO = userObj.toUserVO();
                        Map<String,Object> newClaims = new HashMap<>();
                        newClaims.put("USER",user);
                        newClaims.put("ROLE", userVO.getRole());
                        newClaims.put("EMAIL",newEmail);
                        String newToken = jwtUtil.generateToken(newClaims,10*60);
                        redisUtil.deleteBatch("onlineUser:" + token,"oldEmailCode:" + userObj.getEmail(),"newEmailCode:" + newEmail);
                        redisUtil.set("onlineUser:" + newToken, userVO,10 ,TimeUnit.MINUTES);
                        return ResultMsg.success(newToken);
                    }
                    return ResultMsg.error("DATASOURCE_ERROR");
                }
                return ResultMsg.error("EMAIL_ERROR");
            }
            return ResultMsg.error("NEW_EMAIL_CODE_ERROR");
        }
        return ResultMsg.error("OLD_EMAIL_CODE_ERROR");
    }

    public ResultMsg findUserByUser(String user){
        UserOtherVO userObj = new UserOtherVO(userRedis.findRedisUser(user),userRedis.findRedisUserPersonalized(user));
        return ResultMsg.success(userObj);
    }

    public ResultMsg exit(HttpSession session,String token){
        try {
            jwtUtil.parseToken(token);
        } catch (Exception e) {
            return ResultMsg.error("NO_TOKEN");
        }
        redisUtil.delete("onlineUser:" + jwtUtil.parseTokenStr(token));
        session.invalidate();
        return ResultMsg.success();
    }

    public ResultMsg findAllSignIn(String token,int page){
        String user = jwtUtil.getTokenField(token,"USER");
        List<SignIn> signInList = userRedis.findRedisSignIn(user,page);
        int count = userRedis.findRedisSignInCount(user);
        return ResultMsg.success(signInList,count);
    }

    private boolean isSignInToday(String user){
        if(redisUtil.hasKey("user:signIn:today" + user + ":" + dateUtil.getDateTimeByFormat("yyyy-MM-dd"))){
            return true;
        }
        return signInDao.countTodaySignIn(user) == 1;
    }

    public ResultMsg checkSignIn(String token){
        String user = jwtUtil.getTokenField(token,"USER");
        return ResultMsg.success(isSignInToday(user),userRedis.findRedisSignInContinualCount(user));
    }

    @Transactional
    public int signIn(String token,String msg,String emotion){
        String user = jwtUtil.getTokenField(token,"USER");
        token = jwtUtil.parseTokenStr(token);
        if(isSignInToday(user)){
            throw new ServiceException("SIGNIN_ERROR","今日已签到");
        }
        var sensitiveWords = SensitiveUtil.getFoundAllSensitive(msg);
        if(!sensitiveWords.isEmpty()){
            throw new ServiceException("SENSITIVE_ERROR","签到内容包含敏感词："+sensitiveWords);
        }
        Random r = new Random();
        int addExp = r.nextInt(50)+20;
        User userObj = userRedis.findRedisUser(user);
        int exp = userObj.getExp() + addExp;
        int level = userObj.getLevel();
        int expDiff = Exp.checkAndLevelUp(exp,level);
        if(expDiff >= 0){
            exp = expDiff;
            level++;
        }
        msg = "[获得"+addExp+"点经验] "+msg;
        LocalDateTime ldt = LocalDateTime.now();
        SignIn signIn = new SignIn(user, null, dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd"),msg,emotion);
        int count = userRedis.findRedisSignInContinualCount(user);
        signInDao.insertSignIn(signIn);
        var previousSignIns = userRedis.findRedisSignIn(user,1);
        //这里的判断比较复杂，如果获取到的count（连续签到数）!= 0，说明存在连续签到记录；如果!previousSignIns.isEmpty()，说明存在签到记录，且可能是连续签到；如果dateUtil.isContinuityOfDate(...)，说明最近一次签到记录的日期与当前日期是连续的，那么才可以继续增加连续签到数，否则重置为1
        boolean isContinual = count != 0 && !previousSignIns.isEmpty() && dateUtil.isContinuityOfDate(LocalDate.parse(previousSignIns.get(0).getDate()),ldt.toLocalDate());
        userRedis.setRedisSignInContinualCount(user, isContinual);
        userRedis.setRedisSignIn(user,signIn);
        userRedis.setRedisSignInCount(user,true);
        redisUtil.set(redisUtil.joinKey("user:signIn:today",user,signIn.getDate()),signIn,1 ,TimeUnit.DAYS);
        String updateTime = dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss");
        userDao.updateExp(user,exp,level,updateTime);
//        这里与上面的判断是一样的，然后让count+1或者重置为1
        count = isContinual ? count + 1 : 1;
        medalService.checkAndGainSignInCount30Or365DayMedal(count,true,user);
        medalService.checkAndGainSignInCount30Or365DayMedal(count,false,user);
        userObj.setExp(exp);
        userObj.setLevel(level);
        userObj.setUpdateTime(updateTime);
        userRedis.setRedisUser(user, userObj);
        var tokenUser = userObj.toUserVO();
        tokenUser.setMaxExp(Exp.getMaxExp(level));
        redisUtil.set(redisUtil.joinKey("onlineUser",token), tokenUser,10 ,TimeUnit.MINUTES);
        return addExp;
    }

    public ResultMsg isExpire(String token){
        try {
            jwtUtil.parseToken(token);
        } catch (ExpiredJwtException e) {
            return ResultMsg.error("EXPIRE_TOKEN");
        }
        return ResultMsg.success();
    }

    public ResultMsg toAdmin( String token){
        Claims claims = jwtUtil.parseToken(token);
        String role = (String) claims.get("ROLE");
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        if(!(role.equals("admin") || userObj.getRole().equals("admin"))){
            return ResultMsg.error("NOT_PERMISSION");
        }
        return ResultMsg.success();
    }

    public UserPersonalized findPersonalized(String token){
        String user = jwtUtil.getTokenField(token,"USER");
        return userRedis.findRedisUserPersonalized(user);
    }

    @Transactional
    public void updatePersonalized(String token,UserPersonalizedDTO personalizedDTO){
        String user = jwtUtil.getTokenField(token,"USER");
        if(!codeUtil.checkCode(personalizedDTO.getCode())){
            throw new ServiceException("CODE_ERROR","验证码错误");
        }
        var sensitiveWords = SensitiveUtil.getFoundAllSensitive(personalizedDTO.getSignature());
        if(sensitiveWords != null && !sensitiveWords.isEmpty()){
            throw new ServiceException("SENSITIVE_ERROR","签名包含敏感词："+sensitiveWords);
        }
        UserPersonalized redisPersonalized = userRedis.findRedisUserPersonalized(user);
        UserPersonalized personalized = personalizedDTO.toUserPersonalized();
        personalized.setUser(user);
        personalized.setUpdateTime(dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
        personalized.setSignature(redisPersonalized.getSignature());
        userRedis.setRedisUserPersonalized(user,personalized);
        userDao.updatePersonalized(personalized);
    }

    @Transactional
    public void updateSignature(String token,String signature,String code){
        String user = jwtUtil.getTokenField(token,"USER");
        if(!codeUtil.checkCode(code)){
            throw new ServiceException("CODE_ERROR","验证码错误");
        }
        var sensitiveWords = SensitiveUtil.getFoundAllSensitive(signature);
        if(!sensitiveWords.isEmpty()){
            throw new ServiceException("SENSITIVE_ERROR","签名包含敏感词："+sensitiveWords);
        }
        UserPersonalized redisPersonalized = userRedis.findRedisUserPersonalized(user);
        redisPersonalized.setSignature(signature);
        redisPersonalized.setUpdateTime(dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
        userRedis.setRedisUserPersonalized(user,redisPersonalized);
        userDao.updateSignature(user,signature,redisPersonalized.getUpdateTime());

    }

    @Autowired
    RestTemplate restTemplate;
    String clientId = "95daccaa-315e-436a-828e-dcad1e99d0e6";

    public String getDeviceCode(String token){

        if(redisUtil.hasKey("minecraft:getDeviceCode:"+token)){
            throw new ServiceException("ALREADY_GET","以获取过验证信息，请1分钟后再试");
        }
        redisUtil.set("minecraft:getDeviceCode:"+token,true,1,TimeUnit.MINUTES);
//        POST/GET https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode
        String url = "https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode";
        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("client_id",clientId);
        body.add("scope","XboxLive.signin offline_access");
        ResponseEntity<String> response = restTemplate.postForEntity(url,body,String.class);
        return response.getBody();
    }

    public String getMicrosoftToken(String token,String deviceCode) {
//        POST https://login.microsoftonline.com/consumers/oauth2/v2.0/token
        String url = "https://login.microsoftonline.com/consumers/oauth2/v2.0/token";
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/x-www-form-urlencoded");
        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("client_id",clientId);
        body.add("grant_type","urn:ietf:params:oauth:grant-type:device_code");
        body.add("device_code",deviceCode);
        try{
            ResponseEntity<String> response = restTemplate.postForEntity(url,body,String.class,headers);
            return response.getBody();
        }
        catch (Exception e){
            if(e.getMessage().contains("authorization_pending")){
                throw new ServiceException("AUTHORIZATION_PENDING","请先完成授权");
            }
            else if(e.getMessage().contains("slow_down")){
                throw new ServiceException("SLOW_DOWN","请求过于频繁，请稍后再试");
            }
            else{
                log.info("getMicrosoftToken error:{}",e.getMessage());
                throw new ServiceException("GET_TOKEN_ERROR","获取token失败");
            }
        }
    }

    public String minecraftVerify(String minecraftToken,String token) {
        log.info("minecraftVerify minecraftToken:{}",minecraftToken);
        String user = jwtUtil.getTokenField(token,"USER");
//        GET https://api.minecraftservices.com/minecraft/profile
        String url = "https://api.minecraftservices.com/minecraft/profile";
        RequestEntity<Void> requestEntity = RequestEntity
                .get(URI.create(url))
                .header("Authorization", "Bearer " + minecraftToken)
                .build();
        String response ;
        try{
            response = restTemplate.exchange(requestEntity,String.class).getBody();
        }
        catch (Exception e){
            if(e.getMessage().contains("/minecraft/profile")){
                throw new ServiceException("VERIFY_FAILED","验证失败，请检查token是否正确");
            }
            throw new ServiceException("VERIFY_ERROR","验证失败，请稍后再试");
        }
        medalService.checkAndGainMinecraftPlayer(response,user);
        log.info("minecraftVerify success:{}",user);
        return response;
    }
}
