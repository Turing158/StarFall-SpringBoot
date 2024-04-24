package com.starfall.service;

import com.starfall.dao.SignInDao;
import com.starfall.dao.UserDao;
import com.starfall.entity.*;
import com.starfall.util.*;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    SignInDao signInDao;
    @Autowired
    AECSecure aecSecure;
    @Autowired
    MailUtil mailUtil;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisUtil redisUtil;
    public ResultMsg login(HttpSession session,String account, String password,String code) {
        String sessionCode = (String) session.getAttribute("code");
        if(sessionCode.equals(code)){
            boolean flag = account.contains("@");
            if(flag){
                if(userDao.existEmail(account) == 1){
                    return loginSuccess(account, password);
                }
                return ResultMsg.error("EMAIL_ERROR");
            }
            if(userDao.existUser(account) == 1){
                return loginSuccess(account, password);
            }
            return ResultMsg.error("USER_ERROR");
        }
        return ResultMsg.error("CODE_ERROR");
    }

    private ResultMsg loginSuccess(String account, String password){
        User user = userDao.findByUserOrEmail(account);
        if(user.getPassword().equals(aecSecure.encrypt(password))){
            Map<String,Object> claims = new HashMap<>();
            claims.put("USER",user.getUser());
            claims.put("EMAIL",user.getEmail());
            String token = JwtUtil.generateJwt(claims);
            redisUtil.set(token,user,1, TimeUnit.DAYS);
            return ResultMsg.success(token);
        }
        return ResultMsg.error("PASSWORD_ERROR");
    }


    public ResultMsg getUserInfo(String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj;
        if(redisUtil.hasKey(token)){
            userObj = (User) redisUtil.get(token);
        }
        else{
            userObj = userDao.findByUserOrEmail(user);
        }
        UserOut userOut = new UserOut(
                userObj.getUser(),
                userObj.getName(),
                userObj.getGender(),
                userObj.getEmail(),
                userObj.getBirthday(),
                userObj.getExp(),
                userObj.getLevel(),
                userObj.getAvatar()
        );
        return ResultMsg.success(userOut);
    }



    public ResultMsg logout(HttpSession session){
        session.invalidate();
        return ResultMsg.success();
    }
    public ResultMsg register(HttpSession session,String user, String password, String email,String emailCode,String code){
        if(userDao.existUser(user) == 0){
            if(userDao.existEmail(email) == 0){
                String emailCodeSession = session.getAttribute("emailCode").toString();
                if(emailCodeSession.equals(emailCode.toUpperCase())){
                    AECSecure aecSecure = new AECSecure();
                    LocalDateTime ldt = LocalDateTime.now();
                    String date = ldt.getYear() + "-" + ldt.getMonthValue() + "-" + ldt.getDayOfMonth();
                    String name = "新用户"+ldt.getYear() + ldt.getMonthValue() + ldt.getDayOfMonth();
                    User userObj = new User(user, aecSecure.encrypt(password), name, 0,email, date, 0, 1,"",null);
                    userDao.insertUser(userObj);
                    return ResultMsg.success();
                }
                return ResultMsg.error("EMAIL_CODE_ERROR");
            }
            return ResultMsg.error("EMAIL_ERROR");
        }
        return ResultMsg.error("USER_ERROR");
    }
    public ResultMsg getEmailCode(HttpSession session, String email){
        String code = CodeUtil.getCode(6);
        mailUtil.reg_mail(email,code.toUpperCase());
        session.setAttribute("emailCode",code.toUpperCase());
        return ResultMsg.success();
    }



    public ResultMsg checkForgetPassword(HttpSession session,String email,String emailCode,String code){
        String codeSession = (String) session.getAttribute("code");
        if(codeSession.equals(code)){
            String emailCodeSession = (String) session.getAttribute("emailCode");
            if(emailCodeSession.equals(emailCode.toUpperCase())){
                User user = userDao.findByUserOrEmail(email);
                if(user != null) {
                    Map<String,Object> claims = new HashMap<>();
                    claims.put("USER",user.getUser());
                    claims.put("EMAIL",user.getEmail());
                    claims.put("CODE",emailCodeSession);
                    String token = JwtUtil.generateJwt(claims);
                    return ResultMsg.success(token);
                }
                return ResultMsg.error("EMAIL_ERROR");
            }
            return ResultMsg.error("EMAIL_CODE_ERROR");
        }
        return ResultMsg.error("CODE_ERROR");
    }

    public ResultMsg forgetPassword(HttpSession session,String token,String password){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        String email = (String) claims.get("EMAIL");
        String code = (String) claims.get("CODE");
        String emailCodeSession = (String) session.getAttribute("emailCode");
        if(emailCodeSession.equals(code.toUpperCase())){
            userDao.updatePassword(user,aecSecure.encrypt(password));
            return ResultMsg.success();
        }
        return ResultMsg.error("EMAIL_CODE_ERROR");
    }



    public ResultMsg settingInfo(HttpSession session,String token,String name,int gender,String birthday,String code){
        String codeSession = (String) session.getAttribute("code");
        if(codeSession.equals(code)){
            Claims claims = JwtUtil.parseJWT(token);
            String user = (String) claims.get("USER");
            int status = userDao.updateInfo(user,name,gender,birthday);
            if(status == 1){
                User userObj;
                if(redisUtil.hasKey(token)){
                    userObj = (User) redisUtil.get(token);
                }
                else{
                    userObj = userDao.findByUserOrEmail(user);
                }
                UserOut userOut = new UserOut(
                        userObj.getUser(),
                        userObj.getName(),
                        userObj.getGender(),
                        userObj.getEmail(),
                        userObj.getBirthday(),
                        userObj.getExp(),
                        userObj.getLevel(),
                        userObj.getAvatar()
                );
                return ResultMsg.success(userOut);
            }
            return ResultMsg.error("DATASOURCE_ERROR");
        }
        return ResultMsg.error("CODE_ERROR");
    }


    public ResultMsg settingPassword(HttpSession session,String token,String oldPassword,String newPassword,String code){
        String codeSession = (String) session.getAttribute("code");
        if(codeSession.equals(code)){
            Claims claims = JwtUtil.parseJWT(token);
            String user = (String) claims.get("USER");
            User userObj;
            if(redisUtil.hasKey(token)){
                userObj = (User) redisUtil.get(token);
            }
            else{
                userObj = userDao.findByUserOrEmail(user);
            }
            String encryptOldPassword = aecSecure.encrypt(oldPassword);
            if(userObj.getPassword().equals(encryptOldPassword)){
                String encryptNewPassword = aecSecure.encrypt(newPassword);
                int status = userDao.updatePassword(user,encryptNewPassword);
                if(status == 1){
                    return ResultMsg.success();
                }
                return ResultMsg.error("DATASOURCE_ERROR");
            }
            return ResultMsg.error("PASSWORD_ERROR");
        }
        return ResultMsg.error("CODE_ERROR");
    }

    @Value("${avatar.sava.path}")
    String avatarSavePath = "";
    public ResultMsg settingAvatar(String token,String avatarBase64){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj;
        if(redisUtil.hasKey(token)){
            userObj = (User) redisUtil.get(token);
        }
        else{
            userObj = userDao.findByUserOrEmail(user);
        }
        String oldAvatar = userObj.getAvatar();
        String avatarOutHead = "data:image/png;base64,";
        if(avatarBase64.startsWith(avatarOutHead)){
            avatarBase64 = avatarBase64.substring(avatarOutHead.length());
        }
        byte[] bytes = Base64.getDecoder().decode(avatarBase64);
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {// 调整异常数据
                bytes[i] += 256;
            }
        }
        LocalDateTime ldt = LocalDateTime.now();
        String date = ldt.getYear()  + DateUtil.fillZero(ldt.getMonthValue()+1) + DateUtil.fillZero(ldt.getDayOfMonth()) + DateUtil.fillZero(ldt.getHour()) + DateUtil.fillZero(ldt.getMinute()) + DateUtil.fillZero(ldt.getSecond()) + DateUtil.fillZero(ldt.getNano());
        String avatarName = date + user;
        String fileName = avatarName + ".png";
        try {
            OutputStream out = new FileOutputStream(avatarSavePath + "/" + fileName);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!oldAvatar.equals("default.png")){
            File deleteFile = new File(avatarSavePath + "/" + oldAvatar);
            deleteFile.delete();
        }
        userDao.updateAvatar(user,fileName);
        userObj.setAvatar(fileName);
        redisUtil.set(token,userObj,1,TimeUnit.DAYS);
        return ResultMsg.success(fileName);
    }

    public ResultMsg sendOldEmailCode(HttpSession session,String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        String code = CodeUtil.getCode(6);
        session.setAttribute("oldEmailCode",code.toUpperCase());
        mailUtil.custom_mail(userObj.getEmail(),"修改旧邮箱",code.toUpperCase());
        return ResultMsg.success();
    }

    public ResultMsg sendNewEmailCode(HttpSession session,String email){
        String code = CodeUtil.getCode(6);
        session.setAttribute("newEmailCode",code.toUpperCase());
        mailUtil.custom_mail(email,"新邮箱",code.toUpperCase());
        return ResultMsg.success();
    }

    public ResultMsg settingEmail(HttpSession session,String token,String newEmail,String oldEmailCode,String newEmailCode){
        String oldEmailCodeSession = session.getAttribute("oldEmailCode").toString();
        String newEmailCodeSession = session.getAttribute("newEmailCode").toString();
        if(oldEmailCodeSession.equals(oldEmailCode.toUpperCase())){
            if(newEmailCodeSession.equals(newEmailCode.toUpperCase())){
                Claims claims = JwtUtil.parseJWT(token);
                String user = (String) claims.get("USER");
                if(userDao.existEmail(newEmail) == 0){
                    int status = userDao.updateEmail(user,newEmail);
                    if(status == 1){
                        Map<String,Object> newClaims = new HashMap<>();
                        newClaims.put("USER",user);
                        newClaims.put("EMAIL",newEmail);
                        String newToken = JwtUtil.generateJwt(newClaims);
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
        UserOut userObj = userDao.findByUser(user);
        if(userObj != null){
            userObj.orderMaxExp();
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
        session.invalidate();
        return ResultMsg.success();
    }


    public ResultMsg findAlreadySignIn(String token,int page){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        List<SignIn> signInList = signInDao.findAllSignInByUser(user,(page-1)*6);
        int count = 1;
        if(signInList.size() == 1){
            count = 1;
        }
        else if(signInList.isEmpty()){
            count = 0;
        }
        else{
            for (int i = 0; i < signInList.size()-1; i++) {
                LocalDate newDate = LocalDate.parse(signInList.get(i).getDate());
                LocalDate oldDate = LocalDate.parse(signInList.get(i+1).getDate());
                if(DateUtil.isContinuityOfDate(oldDate,newDate)){
                    count++;
                }
                else {
                    break;
                }
            }
        }
        return ResultMsg.success(signInList,count);
    }
    public ResultMsg findSignInCount(String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        int count = signInDao.countSignInByUser(user);
        return ResultMsg.success(count);
    }

    public ResultMsg signIn(String token,String msg,String emotion){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        LocalDateTime ldt = LocalDateTime.now();
        String date = ldt.getYear() + "-" + ldt.getMonthValue() + "-" + ldt.getDayOfMonth();
        SignIn signIn = signInDao.findSignInByUser(user,date);
        if(signIn == null){
            Random r = new Random();
            int addExp = r.nextInt(50)+20;
            User userObj;
            if(redisUtil.hasKey(token)){
                userObj = (User) redisUtil.get(token);
            }
            else{
                userObj = userDao.findByUserOrEmail(user);
            }
            int exp = userObj.getExp() + addExp;
            int level = userObj.getLevel();
            int expDiff = Exp.checkAndLevelUp(exp,level);
            if(expDiff >= 0){
                exp = expDiff;
                level++;
            }
            msg = "[获得"+exp+"点经验] "+msg;
            signInDao.insertSignIn(user,date,msg,emotion);
            userDao.updateExp(user,exp,level);

            return ResultMsg.success(addExp);
        }
        return ResultMsg.error("SIGNIN_ERROR");
    }



    public User findUserObjByUser(String user){
        return userDao.findByUserOrEmail(user);
    }

}
