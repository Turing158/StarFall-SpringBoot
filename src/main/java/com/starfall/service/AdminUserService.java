package com.starfall.service;

import com.starfall.dao.AdminUserDao;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
}
