package com.starfall.service;

import com.starfall.dao.SignInDao;
import com.starfall.dao.UserDao;
import com.starfall.entity.*;
import com.starfall.util.*;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
            signInDao.insertSignIn(user,date,msg,emotion);
            Random r = new Random();
            int addExp = r.nextInt(50)+20;
            User userObj = userDao.findByUserOrEmail(user);
            int exp = userObj.getExp() + addExp;
            int level = userObj.getLevel();
            int expDiff = Exp.checkAndLevelUp(exp,level);
            if(expDiff >= 0){
                exp = expDiff;
                level++;
            }
            userDao.updateExp(user,exp,level);
            return ResultMsg.success(addExp);
        }
        return ResultMsg.error("SIGNIN_ERROR");
    }


    public User findUserObjByUser(String user){
        return userDao.findByUserOrEmail(user);
    }

}
