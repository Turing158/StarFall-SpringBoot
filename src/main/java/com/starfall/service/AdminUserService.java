package com.starfall.service;

import com.starfall.dao.AdminUserDao;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.User;
import com.starfall.util.AECSecure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

}


