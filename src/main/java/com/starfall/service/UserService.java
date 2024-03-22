package com.starfall.service;

import com.starfall.dao.UserDao;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.User;
import com.starfall.entity.UserOut;
import com.starfall.util.AECSecure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    AECSecure aecSecure;
    public ResultMsg login(String account, String password) {
        ResultMsg resultMsg = new ResultMsg();
        String match = "/@/g";
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
                    user.getLevel()
            );
            resultMsg.setData(userOut);
        }
        return resultMsg;
    }

    public ResultMsg register(String user, String password, String email){
        ResultMsg resultMsg = new ResultMsg();
        if(userDao.existUser(user) == 0){
            if(userDao.existEmail(email) == 0){
                AECSecure aecSecure = new AECSecure();
                LocalDateTime ldt = LocalDateTime.now();
                String date = ldt.getYear() + "-" + ldt.getMonthValue() + "-" + ldt.getDayOfMonth();
                String name = "新用户"+ldt.getYear() + ldt.getMonthValue() + ldt.getDayOfMonth();
                User userObj = new User(user, aecSecure.encrypt(password), name, 0,email, date, 0, 1);
                userDao.insertUser(userObj);
                resultMsg.setMsg("SUCCESS");
                return resultMsg;
            }
            resultMsg.setMsg("EMAIL_ERROR");
            return resultMsg;
        }
        resultMsg.setMsg("USER_ERROR");
        return resultMsg;
    }


    public ResultMsg settingInfo(String user,String name,String gender,String birthday){
        ResultMsg resultMsg = new ResultMsg();
        int status = userDao.updateInfo(user,name,gender,birthday);
        if(status == 1){
            resultMsg.setMsg("SUCCESS");
            return resultMsg;
        }
        resultMsg.setMsg("DATASOURCE_ERROR");
        return resultMsg;
    }


    public ResultMsg settingPassword(String user,String oldPassword,String newPassword){
        ResultMsg resultMsg = new ResultMsg();
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

}
