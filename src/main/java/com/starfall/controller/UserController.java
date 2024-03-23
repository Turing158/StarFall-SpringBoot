package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {


    @Autowired
    UserService userService;
    @PostMapping("/login")
    public ResultMsg login(HttpSession session,String account, String password,String code){
        return userService.login(session,account,password,code);
    }

    @PostMapping("/register")
    public ResultMsg register(HttpSession session,String user, String password,String email, String emailCode,String code){
        System.out.println(user);
        return userService.register(session,user,password,email,emailCode,code);
    }
    @PostMapping("/getEmailCode")
    public ResultMsg getEmailCode(HttpSession session, String email){
        return userService.getEmailCode(session,email);
    }
}
