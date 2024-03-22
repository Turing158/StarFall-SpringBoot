package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {


    @Autowired
    UserService userService;
    @PostMapping("/login")
    public ResultMsg login(String account, String password,String code){
        return userService.login(account,password,code);
    }
}
