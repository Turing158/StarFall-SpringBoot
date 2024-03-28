package com.starfall.service;

import com.starfall.dao.UserDao;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.User;
import com.starfall.entity.UserOut;
import com.starfall.util.AECSecure;
import com.starfall.util.CodeUtil;
import com.starfall.util.MailUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    AECSecure aecSecure;
    @Autowired
    MailUtil mailUtil;
    public ResultMsg login(HttpSession session,String account, String password,String code) {
        ResultMsg resultMsg = new ResultMsg();
        String sessionCode = (String) session.getAttribute("code");
        System.out.println(session.getId());
        if(sessionCode.equals(code)){
            String match = "\\w*@\\w*";
            boolean flag = account.matches(match);
            if(flag){
                if(userDao.existUser(account) == 1){
                    return loginSuccess(account, password, resultMsg);
                }
                resultMsg.setMsg("EMAIL_ERROR");
                return resultMsg;
            }
            if(userDao.existUser(account) == 1){
                return loginSuccess(account, password, resultMsg);
            }
            resultMsg.setMsg("USER_ERROR");
            return resultMsg;
        }
        resultMsg.setMsg("CODE_ERROR");
        return resultMsg;
    }

    private ResultMsg loginSuccess(String account, String password, ResultMsg resultMsg){
        User user = userDao.findByUserOrEmail(account);
        if(user.getPassword().equals(aecSecure.encrypt(password))){
            resultMsg.setMsg("SUCCESS");
            UserOut userOut = new UserOut(
                    user.getUser(),
                    user.getName(),
                    user.getGender(),
                    user.getEmail(),
                    user.getBirthday(),
                    user.getExp(),
                    user.getLevel(),
                    user.getAvatar()
            );
            resultMsg.setObject(userOut);
            return resultMsg;
        }
        resultMsg.setMsg("PASSWORD_ERROR");
        return resultMsg;
    }

    public ResultMsg register(HttpSession session,String user, String password, String email,String emailCode,String code){
        ResultMsg resultMsg = new ResultMsg();
        if(userDao.existUser(user) == 0){
            if(userDao.existEmail(email) == 0){
                String emailCodeSession = (String) session.getAttribute("emailCode");
                if(emailCodeSession.equals(emailCode.toUpperCase())){
                    AECSecure aecSecure = new AECSecure();
                    LocalDateTime ldt = LocalDateTime.now();
                    String date = ldt.getYear() + "-" + ldt.getMonthValue() + "-" + ldt.getDayOfMonth();
                    String name = "新用户"+ldt.getYear() + ldt.getMonthValue() + ldt.getDayOfMonth();
                    User userObj = new User(user, aecSecure.encrypt(password), name, 0,email, date, 0, 1,"");
                    userDao.insertUser(userObj);
                    resultMsg.setMsg("SUCCESS");
                    return resultMsg;
                }
                resultMsg.setMsg("EMAIL_CODE_ERROR");
                return resultMsg;
            }
            resultMsg.setMsg("EMAIL_ERROR");
            return resultMsg;
        }
        resultMsg.setMsg("USER_ERROR");
        return resultMsg;
    }
    public ResultMsg getEmailCode(HttpSession session, String email){
        ResultMsg resultMsg = new ResultMsg();
        int status = userDao.existEmail(email);
        if(status == 0){
            String code = CodeUtil.getCode(6);
            mailUtil.reg_mail(email,code);
            session.setAttribute("emailCode",code.toUpperCase());
            resultMsg.setMsg("SUCCESS");
            return resultMsg;
        }
        resultMsg.setMsg("EMAIL_ERROR");
        return resultMsg;
    }

    public ResultMsg settingInfo(HttpSession session,String user,String name,String gender,String birthday,String code){
        ResultMsg resultMsg = new ResultMsg();
        String codeSession = (String) session.getAttribute("code");
        if(codeSession.equals(code)){
            int status = userDao.updateInfo(user,name,gender,birthday);
            if(status == 1){
                User userObj = userDao.findByUserOrEmail(user);
                resultMsg.setObject(new UserOut(
                        userObj.getUser(),
                        userObj.getName(),
                        userObj.getGender(),
                        userObj.getEmail(),
                        userObj.getBirthday(),
                        userObj.getExp(),
                        userObj.getLevel(),
                        userObj.getAvatar()
                ));
                resultMsg.setMsg("SUCCESS");
                return resultMsg;
            }
            resultMsg.setMsg("DATASOURCE_ERROR");
            return resultMsg;
        }
        resultMsg.setMsg("CODE_ERROR");
        return resultMsg;
    }


    public ResultMsg settingPassword(HttpSession session,String user,String oldPassword,String newPassword,String code){
        ResultMsg resultMsg = new ResultMsg();
        String codeSession = (String) session.getAttribute("code");
        if(codeSession.equals(code)){
            User userObj = userDao.findByUserOrEmail(user);
            String encryptOldPassword = aecSecure.encrypt(oldPassword);
            if(userObj.getPassword().equals(encryptOldPassword)){
                String encryptNewPassword = aecSecure.encrypt(newPassword);
                int status = userDao.updatePassword(user,encryptNewPassword);
                if(status == 1){
                    resultMsg.setMsg("SUCCESS");
                    return resultMsg;
                }
                resultMsg.setMsg("DATASOURCE_ERROR");
                return resultMsg;
            }
            resultMsg.setMsg("PASSWORD_ERROR");
            return resultMsg;
        }
        resultMsg.setMsg("CODE_ERROR");
        return resultMsg;
    }


    public ResultMsg findUserByUser(String user){
        ResultMsg resultMsg = new ResultMsg();
        UserOut userObj = userDao.findByUser(user);
        if(userObj != null){
            userObj.orderMaxExp();
            resultMsg.setObject(userObj);
            resultMsg.setMsg("SUCCESS");
            return resultMsg;
        }
        resultMsg.setMsg("USER_ERROR");
        return resultMsg;
    }

}
