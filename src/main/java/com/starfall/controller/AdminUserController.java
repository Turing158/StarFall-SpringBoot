package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.entity.SignIn;
import com.starfall.entity.User;
import com.starfall.service.AdminUserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {
    @Autowired
    private AdminUserService userService;


    @PostMapping("/adminFindAllUsersForSelect")
    public ResultMsg findAllUsersForSelect() {
        return userService.findAllUsersForSelect();
    }

    @PostMapping("/adminFindAllUsers")
    public ResultMsg findAllUsers(int page) {
        return userService.findAllUsers(page);
    }

    @PostMapping("/adminInsertUser")
    public ResultMsg insertUser(@RequestBody User user) {
        return userService.insertUser(user);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static
    class UserAndOldUser{
        User user;
        String oldUser;
        String oldEmail;
    }

    @PostMapping("/adminUpdateUser")
    public ResultMsg updateUser(@RequestBody UserAndOldUser user) {
        return userService.updateUser(user.getUser(),user.getOldUser(),user.getOldEmail());
    }

    @PostMapping("/adminDeleteUser")
    public ResultMsg deleteUser(String user) {
        return userService.deleteUser(user);
    }

    @PostMapping("/adminUpdateAvatar")
    public ResultMsg updateAvatar(String user,String avatar) {
        return userService.updateAvatar(user,avatar);
    }

    @PostMapping("/adminFindAllSignIn")
    public ResultMsg findAllSignIn(int page) {
        return userService.findAllSignIn(page);
    }

    @PostMapping("/adminAppendSignIn")
    public ResultMsg appendSignIn(@RequestBody SignIn signIn) {
        return userService.appendSignIn(signIn);
    }

    @PostMapping("/adminUpdateSignIn")
    public ResultMsg updateSignIn(@RequestBody SignIn signIn) {
        return userService.updateSignIn(signIn);
    }

    @PostMapping("/adminDeleteSignIn")
    public ResultMsg deleteSignIn(@RequestBody SignIn signIn) {
        return userService.deleteSignIn(signIn);
    }




}

