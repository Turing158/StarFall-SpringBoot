package com.starfall.service;

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
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    FileService fileService;
    @Autowired
    SearchService searchService;
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

    public ResultMsg login(String account, String password,String code) {
        if(codeUtil.checkCode(code)){
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
        if(user.getPassword().equals(aecSecureUtil.encrypt(password))){
            Map<String,Object> claims = new HashMap<>();
            claims.put("USER",user.getUser());
            claims.put("EMAIL",user.getEmail());
            claims.put("ROLE",user.getRole());
            String token = JwtUtil.generateJwt(claims);
            redisUtil.set("onlineUser:" + token,user.toUserDTO(),1, TimeUnit.DAYS);
            return ResultMsg.success(token);
        }
        return ResultMsg.error("PASSWORD_ERROR");
    }


    public ResultMsg getUserInfo(String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        UserDTO userObj;
        if(redisUtil.hasKey("onlineUser:" + token)){
            userObj = redisUtil.get("onlineUser:" + token, UserDTO.class);
        }
        else{
            userObj = userDao.findByUserOrEmail(user).toUserDTO();
        }

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
                        //这里记得添加关于UserPersonalized
                        userDao.insertUser(userObj);
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



    public ResultMsg settingInfo(String token,String name,int gender,String birthday,String code){
        if(codeUtil.checkCode(code)){
            Claims claims = JwtUtil.parseJWT(token);
            String user = (String) claims.get("USER");
            int status = userDao.updateInfo(user,name,gender,birthday, dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
            if(status == 1){
                UserDTO userObj = userDao.findByUserOrEmail(user).toUserDTO();
                redisUtil.set("onlineUser:" + token,userObj);
                searchService.saveUserName(user,name);
                return ResultMsg.success(userObj);
            }
            return ResultMsg.error("DATASOURCE_ERROR");
        }
        return ResultMsg.error("CODE_ERROR");
    }


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

    public ResultMsg settingAvatar(String token,String avatarBase64){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        UserDTO userObj;
        if(redisUtil.hasKey("onlineUser:" + token)){
            userObj = redisUtil.get("onlineUser:" + token, UserDTO.class);
        }
        else{
            userObj = userDao.findByUserOrEmail(user).toUserDTO();
        }
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
        redisUtil.set("onlineUser:" + token,userObj);
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

    public ResultMsg settingEmail(String token,String newEmail,String oldEmailCode,String newEmailCode){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        if(codeUtil.checkEmailCode("oldEmailCode:",userObj.getEmail(),oldEmailCode)){
            if(codeUtil.checkEmailCode("newEmailCode:",newEmail,newEmailCode)){
                if(userDao.existEmail(newEmail) == 0){
                    int status = userDao.updateEmail(user,newEmail, dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
                    if(status == 1){
                        UserDTO userDTO = userDao.findByUser(user);
                        Map<String,Object> newClaims = new HashMap<>();
                        newClaims.put("USER",user);
                        newClaims.put("ROLE", userDTO.getRole());
                        newClaims.put("EMAIL",newEmail);
                        String newToken = JwtUtil.generateJwt(newClaims);
                        redisUtil.deleteBatch("onlineUser:" + token,"oldEmailCode:" + userObj.getEmail(),"newEmailCode:" + newEmail);
                        redisUtil.set("onlineUser:" + newToken, userDTO);
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
        UserDTO userObj = userDao.findByUser(user);
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
                if(dateUtil.isContinuityOfDate(oldDate,newDate)){
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
        String date = dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd");
        SignIn signIn = signInDao.findSignInByUser(user,date);
        if(signIn == null){
            Random r = new Random();
            int addExp = r.nextInt(50)+20;
            UserDTO userObj;
            if(redisUtil.hasKey("onlineUser:" + token)){
                userObj = redisUtil.get("onlineUser:" + token, UserDTO.class);
            }
            else{
                userObj = userDao.findByUserOrEmail(user).toUserDTO();
            }
            int exp = userObj.getExp() + addExp;
            int level = userObj.getLevel();
            int expDiff = Exp.checkAndLevelUp(exp,level);
            if(expDiff >= 0){
                exp = expDiff;
                level++;
            }
            msg = "[获得"+addExp+"点经验] "+msg;
            signInDao.insertSignIn(user,date,msg,emotion);
            userDao.updateExp(user,exp,level,dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
            userObj.setExp(exp);
            userObj.setLevel(level);
            userObj.setMaxExp(Exp.getMaxExp(level));
            redisUtil.set("onlineUser:" + token,userObj);
            return ResultMsg.success(addExp);
        }
        return ResultMsg.error("SIGNIN_ERROR");
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

    public User findUserObjByUser(String user){
        return userDao.findByUserOrEmail(user);
    }

}
