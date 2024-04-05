package com.starfall.service;

import com.starfall.dao.UserDao;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.User;
import com.starfall.entity.UserOut;
import com.starfall.util.*;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    AECSecure aecSecure;
    @Autowired
    MailUtil mailUtil;
    public ResultMsg login(HttpSession session,String account, String password,String code) {
        String sessionCode = (String) session.getAttribute("code");
        System.out.println(session.getId());
        if(sessionCode.equals(code)){
            String match = "\\w*@\\w*";
            boolean flag = account.matches(match);
            if(flag){
                if(userDao.existUser(account) == 1){
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
            return ResultMsg.success(token);
        }
        return ResultMsg.error("PASSWORD_ERROR");
    }


    public ResultMsg getUserInfo(String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
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
                String emailCodeSession = (String) session.getAttribute("emailCode");
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
        int status = userDao.existEmail(email);
        if(status == 0){
            String code = CodeUtil.getCode(6);
            mailUtil.reg_mail(email,code);
            session.setAttribute("emailCode",code.toUpperCase());
            return ResultMsg.success();
        }
        return ResultMsg.error("EMAIL_ERROR");
    }

    public ResultMsg settingInfo(HttpSession session,String token,String name,int gender,String birthday,String code){
        String codeSession = (String) session.getAttribute("code");
        if(codeSession.equals(code)){
            Claims claims = JwtUtil.parseJWT(token);
            String user = (String) claims.get("USER");
            int status = userDao.updateInfo(user,name,gender,birthday);
            if(status == 1){
                User userObj = userDao.findByUserOrEmail(user);
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
            User userObj = userDao.findByUserOrEmail(user);
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

    public User findUserObjByUser(String user){
        return userDao.findByUserOrEmail(user);
    }

}
