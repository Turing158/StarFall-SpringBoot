package com.starfall.service;

import com.starfall.dao.AdminUserDao;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.SignIn;
import com.starfall.entity.User;
import com.starfall.util.AECSecure;
import com.starfall.util.DateUtil;
import com.starfall.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class AdminUserService {
    @Autowired
    private AdminUserDao userDao;


    public ResultMsg findAllUsersForSelect() {
        List<User> users = userDao.findAllUser();
        List<User> newUsers = new ArrayList<>();
        users.forEach(user -> {
            User newUser = new User();
            newUser.setUser(user.getUser());
            newUser.setName(user.getName());
            newUsers.add(newUser);
        });
        return ResultMsg.success(newUsers);
    }

    public ResultMsg findAllUsers(int page) {
        List<User> users = userDao.findUserByPage((page-1)*10);
        users.forEach(user -> {
            user.setPassword("***");
        });
        return ResultMsg.success(users,userDao.countUser());
    }

    @Autowired
    AECSecure aecSecure;

    public ResultMsg insertUser(User user) {
        System.out.println(user);
        if(userDao.existUser(user.getUser()) == 0){
            if(userDao.existEmail(user.getEmail()) == 0){
                user.setAvatar("default.png");
                user.setPassword(aecSecure.encrypt(user.getPassword()));
                int status = userDao.insertUser(user);
                return status == 1 ? ResultMsg.success() : ResultMsg.error("DATASOURCE_ERROR");
            }
            return ResultMsg.error("EMAIL_EXIST");
        }
        return ResultMsg.error("USER_EXIST");
    }


    public ResultMsg updateUser(User user,String oldUser,String oldEmail) {
        if(userDao.existUser(oldUser) == 1){
            if(Objects.equals(user.getUser(), oldUser) || userDao.existUser(user.getUser()) == 0){
                if(Objects.equals(user.getEmail(), oldEmail) || userDao.existEmail(user.getEmail()) == 0){
                    user.setPassword(aecSecure.encrypt(user.getPassword()));
                    int status = userDao.updateUser(user,oldUser);
                    return status == 1 ? ResultMsg.success() : ResultMsg.error("DATASOURCE_ERROR");
                }
                return ResultMsg.error("EMAIL_EXIST");
            }
            return ResultMsg.error("USER_EXIST");
        }
        return ResultMsg.error("USER_NOT_EXIST");
    }

    public ResultMsg deleteUser(String user) {
        if(userDao.existUser(user) == 1){
            int status = userDao.deleteUser(user);
            return status == 1 ? ResultMsg.success() : ResultMsg.error("DATASOURCE_ERROR");
        }
        return ResultMsg.error("USER_NOT_EXIST");
    }



    @Value("${avatar.sava.path}")
    String avatarSavePath = "";
    public ResultMsg updateAvatar(String user,String avatar){
        if(avatar.equals("default.png")){
            userDao.updateAvatar(user,"default.png");
            return ResultMsg.success("default.png");
        }
        User userObj = userDao.findUserByUser(user);
        String oldAvatar = userObj.getAvatar();
        String avatarOutHead = "data:image/png;base64,";
        if(avatar.startsWith(avatarOutHead)){
            avatar = avatar.substring(avatarOutHead.length());
        }
        byte[] bytes = Base64.getDecoder().decode(avatar);
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
        return ResultMsg.success(fileName);
    }

    public ResultMsg findAllSignIn(int page){
        return ResultMsg.success(userDao.findSignInByPage((page-1)*10),userDao.countSignIn());
    }

    public ResultMsg appendSignIn(SignIn signIn){
        if(userDao.existSignIn(signIn.getUser(),signIn.getDate()) == 0){
            int status = userDao.insertSignIn(signIn);
            return status == 1 ? ResultMsg.success() : ResultMsg.error("DATASOURCE_ERROR");
        }
        return ResultMsg.error("SIGN_IN_EXIST");
    }

    public ResultMsg updateSignIn(SignIn signIn){
        int status = userDao.updateSignIn(signIn);
        return status == 1 ? ResultMsg.success() : ResultMsg.error("DATASOURCE_ERROR");
    }


    public ResultMsg deleteSignIn(SignIn signIn){
        if(userDao.existSignIn(signIn.getUser(),signIn.getDate()) == 1){
            int status = userDao.deleteSignIn(signIn);
            return status == 1 ? ResultMsg.success() : ResultMsg.error("DATASOURCE_ERROR");
        }
        return ResultMsg.error("SIGN_IN_NOT_EXIST");
    }



}


