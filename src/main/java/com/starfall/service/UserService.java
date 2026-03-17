package com.starfall.service;

import com.starfall.Exception.ServiceException;
import com.starfall.dao.SignInDao;
import com.starfall.dao.UserDao;
import com.starfall.entity.*;
import com.starfall.util.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
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
    AECSecureUtil aecSecureUtil;
    @Autowired
    MailUtil mailUtil;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CodeUtil codeUtil;
    @Autowired
    DateUtil dateUtil;

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
        if(!user.getPassword().equals(aecSecureUtil.encrypt(password))){
            throw new ServiceException("PASSWORD_ERROR", "密码错误");
        }
        Map<String,Object> claims = new HashMap<>();
        claims.put("USER",user.getUser());
        claims.put("EMAIL",user.getEmail());
        claims.put("ROLE",user.getRole());
        String token = JwtUtil.generateJwt(claims);
        medalService.checkAndGainRegisterAlready3year(user.getUser());
        redisUtil.set("onlineUser:" + token,user.toUserVO(),10, TimeUnit.MINUTES);
        return token;
    }

    public ResultMsg getUserInfo(String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        UserVO userObj = getUserObj(token,user);
        return ResultMsg.success(userObj);
    }

    @Transactional
    public ResultMsg register(String user, String password, String email,String emailCode,String code){
        System.out.println(code);
        if(codeUtil.checkCode(code)) {
            if(userDao.existUser(user) == 0){
                if(userDao.existEmail(email) == 0){
                    if(codeUtil.checkEmailCode("regEmailCode:",email,emailCode)) {
                        AECSecureUtil aecSecureUtil = new AECSecureUtil();
                        LocalDateTime ldt = LocalDateTime.now();
                        String date = dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd");
                        String datetime = dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss");
                        String name = "新用户"+dateUtil.getDateTimeByFormat(ldt,"yyyyMMdd");
                        User userObj = new User(user, aecSecureUtil.encrypt(password), name, 0,email, date, 0, 1,"default.png","user",datetime,datetime);
                        UserPersonalized userPersonalized = new UserPersonalized(user,"这个人很懒，什么都没有留下~",null,1,1,1,1,0,datetime,datetime);
                        medalService.gainRegisterMedal(user);
                        userDao.insertUser(userObj);
                        userDao.insertPersonalized(userPersonalized);
                        return ResultMsg.success();
                    }
                    return ResultMsg.error("EMAIL_CODE_ERROR");
                }
                return ResultMsg.error("EMAIL_ERROR");
            }
            return ResultMsg.error("USER_ERROR");
        }
        return ResultMsg.error("CODE_ERROR");
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
                    String token = JwtUtil.generateJwt(claims, 300);
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
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        String email = (String) claims.get("EMAIL");
        String code = (String) claims.get("CODE");
        if(redisUtil.hasKey("forgetPassword:" + email)){
            String redisToken = redisUtil.get("forgetPassword:" + email,String.class);
            redisUtil.delete("forgetPassword:" + email);
            if(!redisToken.equals(token)){
                return ResultMsg.error("TOKEN_ERROR");
            }
        }
        if(codeUtil.checkEmailCode("forgetEmailCode:",email,code,true)){
            userDao.updatePassword(user, aecSecureUtil.encrypt(password), dateUtil.getDateTimeByFormat(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss"));
            return ResultMsg.success();  
        }
        return ResultMsg.error("EMAIL_CODE_ERROR");
    }

    @Transactional
    public ResultMsg settingInfo(String token,String name,int gender,String birthday,String code){
        if(codeUtil.checkCode(code)){
            Claims claims = JwtUtil.parseJWT(token);
            String user = (String) claims.get("USER");
            int status = userDao.updateInfo(user,name,gender,birthday, dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
            if(status == 1){
                UserVO userObj = userDao.findByUserOrEmail(user).toUserVO();
                redisUtil.set("onlineUser:" + token,userObj,30, TimeUnit.MINUTES);
                searchService.saveUserName(user,name);
                return ResultMsg.success(userObj);
            }
            return ResultMsg.error("DATASOURCE_ERROR");
        }
        return ResultMsg.error("CODE_ERROR");
    }

    @Transactional
    public ResultMsg settingPassword(String token,String oldPassword,String newPassword,String code){
        if(codeUtil.checkCode(code)){
            Claims claims = JwtUtil.parseJWT(token);
            String user = (String) claims.get("USER");
            User userObj = userDao.findByUserOrEmail(user);
            String encryptOldPassword = aecSecureUtil.encrypt(oldPassword);
            if(userObj.getPassword().equals(encryptOldPassword)){
                String encryptNewPassword = aecSecureUtil.encrypt(newPassword);
                int status = userDao.updatePassword(user,encryptNewPassword, dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
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
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        UserVO userObj = getUserObj(token,user);
        String oldAvatar = userObj.getAvatar();
        String avatarName = dateUtil.getDateTimeByFormat("yyyyMMddHHmmssSSSS") + CodeUtil.getCode(6);
        String fileName = avatarName + ".png";
        String folder = "user/"+user+"/avatar";
        MultipartFile file = new MultipartFileImpl(CodeUtil.getBase64Bytes(avatarBase64),fileName);
        fileService.upload(file,folder,fileName);
        if(!oldAvatar.equals("default.png")){
            fileService.removeFile(oldAvatar);
        }
        userDao.updateAvatar(user,folder+"/"+fileName, dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
        userObj.setAvatar(folder+"/"+fileName);
        redisUtil.set("onlineUser:" + token,userObj,5, TimeUnit.MINUTES);
        return ResultMsg.success(folder+"/"+fileName);
    }

    public ResultMsg sendOldEmailCode(String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        if(redisUtil.getExpire("oldEmailCode:" + userObj.getEmail()) > 4 * 60){
            return ResultMsg.error("SEND_FAST_ERROR");
        }
        String code = CodeUtil.getCode(6);
        redisUtil.set("oldEmailCode:" + userObj.getEmail(),code.toLowerCase(),5, TimeUnit.MINUTES);
        mailUtil.custom_mail(userObj.getEmail(),"修改旧邮箱",code.toUpperCase());
        return ResultMsg.success();
    }


    public ResultMsg sendNewEmailCode(String token,String email){
        Claims claims = JwtUtil.parseJWT(token);
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
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        if(codeUtil.checkEmailCode("oldEmailCode:",userObj.getEmail(),oldEmailCode)){
            if(codeUtil.checkEmailCode("newEmailCode:",newEmail,newEmailCode)){
                if(userDao.existEmail(newEmail) == 0){
                    int status = userDao.updateEmail(user,newEmail, dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
                    if(status == 1){
                        UserVO userVO = userDao.findByUser(user);
                        Map<String,Object> newClaims = new HashMap<>();
                        newClaims.put("USER",user);
                        newClaims.put("ROLE", userVO.getRole());
                        newClaims.put("EMAIL",newEmail);
                        String newToken = JwtUtil.generateJwt(newClaims);
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
        UserOtherVO userObj = userDao.findByUser(user);
        if(userObj != null){
            userObj.orderMaxExp();
            if(userObj.getShowBirthday() != 1){
                userObj.setBirthday(null);
            }
            if(userObj.getShowOnlineName() != 1){
                userObj.setOnlineName(null);
            }
            if(userObj.getShowGender() != 1){
                userObj.setGender(-1);
            }
            if(userObj.getShowBirthday() != 1){
                userObj.setBirthday(null);
            }
            if(userObj.getShowEmail() != 1){
                userObj.setEmail(null);
            }
            return ResultMsg.success(userObj);
        }
        return ResultMsg.error("USER_ERROR");
    }

    public ResultMsg exit(HttpSession session,String token){
        try {
            JwtUtil.parseJWT(token);
        } catch (Exception e) {
            return ResultMsg.error("NO_TOKEN");
        }
        redisUtil.delete("onlineUser:" + token);
        session.invalidate();
        return ResultMsg.success();
    }

    public ResultMsg findAllSignIn(String token,int page){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        List<SignIn> signInList = signInDao.findAllSignInByUser(user,(page-1)*6);
        int count = signInDao.countSignInByUser(user);
        return ResultMsg.success(signInList,count);
    }

    private boolean isSignInToday(String user){
        if(redisUtil.hasKey("signInToday:" + user + ":" + dateUtil.getDateTimeByFormat("yyyy-MM-dd"))){
            return true;
        }
        return signInDao.countTodaySignIn(user) == 1;
    }

    public ResultMsg checkSignIn(String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        return ResultMsg.success(isSignInToday(user),signInDao.countContinualSignIn(user));
    }

    @Transactional
    public int signIn(String token,String msg,String emotion){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        if(isSignInToday(user)){
            throw new ServiceException("SIGNIN_ERROR","今日已签到");
        }
        Random r = new Random();
        int addExp = r.nextInt(50)+20;
        UserVO userObj = getUserObj(token,user);
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
        signInDao.insertSignIn(signIn);
        redisUtil.set("signInToday:" + user + ":" + signIn.getDate() ,signIn,1 ,TimeUnit.DAYS);
        userDao.updateExp(user,exp,level,dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss"));
        int count = signInDao.countContinualSignIn(user);
        medalService.checkAndGainSignInCount30Or365DayMedal(count,true,user);
        medalService.checkAndGainSignInCount30Or365DayMedal(count,false,user);
        userObj.setExp(exp);
        userObj.setLevel(level);
        userObj.setMaxExp(Exp.getMaxExp(level));
        redisUtil.set("onlineUser:" + token,userObj,10 ,TimeUnit.MINUTES);
        return addExp;
    }

    public ResultMsg isExpire(String token){
        try {
            JwtUtil.parseJWT(token);
        } catch (ExpiredJwtException e) {
            return ResultMsg.error("EXPIRE_TOKEN");
        }
        return ResultMsg.success();
    }

    public ResultMsg toAdmin( String token){
        Claims claims = JwtUtil.parseJWT(token);
        String role = (String) claims.get("ROLE");
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        if(!(role.equals("admin") || userObj.getRole().equals("admin"))){
            return ResultMsg.error("NOT_PERMISSION");
        }
        return ResultMsg.success();
    }

    public UserPersonalized findRedisUserPersonalized(String user){
        UserPersonalized personalized;
        if(redisUtil.hasKey("personalized:" + user)){
            personalized = redisUtil.get("personalized:" + user, UserPersonalized.class);
        }
        else{
            personalized = userDao.findPersonalizedByUser(user);
            redisUtil.set("personalized:" + user, personalized);
        }
        return personalized;
    }

    public UserPersonalized findPersonalized(String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        return findRedisUserPersonalized(user);
    }

    @Transactional
    public void updatePersonalized(String token,UserPersonalizedDTO personalizedDTO){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        if(!codeUtil.checkCode(personalizedDTO.getCode())){
            throw new ServiceException("CODE_ERROR","验证码错误");
        }
        UserPersonalized redisPersonalized = findRedisUserPersonalized(user);
        UserPersonalized personalized = personalizedDTO.toUserPersonalized();
        personalized.setUser(user);
        personalized.setUpdateTime(dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
        userDao.updatePersonalized(personalized);
        personalized.setSignature(redisPersonalized.getSignature());
        redisUtil.set("personalized:" + user, personalized);
    }

    @Transactional
    public void updateSignature(String token,String signature,String code){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        if(!codeUtil.checkCode(code)){
            throw new ServiceException("CODE_ERROR","验证码错误");
        }
        UserPersonalized redisPersonalized = findRedisUserPersonalized(user);
        redisPersonalized.setSignature(signature);
        redisPersonalized.setUpdateTime(dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
        userDao.updateSignature(user,signature,redisPersonalized.getUpdateTime());
        redisUtil.set("personalized:" + user, redisPersonalized);
    }

    private UserVO getUserObj(String token,String user){
        if(redisUtil.hasKey("onlineUser:" + token)){
            return redisUtil.get("onlineUser:" + token, UserVO.class);
        }
        return userDao.findByUserOrEmail(user).toUserVO();
    }
}
