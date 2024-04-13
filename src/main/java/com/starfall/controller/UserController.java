package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {


    @Autowired
    UserService userService;
    @PostMapping("/login")
    public ResultMsg login(HttpSession session,String account, String password,String code){
        return userService.login(session,account,password,code);
    }

    @PostMapping("/getUserInfo")
    public ResultMsg getUserInfo(@RequestHeader("Authorization") String token){
        return userService.getUserInfo(token);
    }

    @PostMapping("/register")
    public ResultMsg register(HttpSession session,String user, String password,String email, String emailCode,String code){
        return userService.register(session,user,password,email,emailCode,code);
    }
    @PostMapping("/getEmailCode")
    public ResultMsg getEmailCode(HttpSession session, String email){
        return userService.getEmailCode(session,email);
    }


    @PostMapping("/findUserByUser")
    public ResultMsg findUserByUser(String user){
        return userService.findUserByUser(user);
    }


    @PostMapping("/updateUserInfo")
    public ResultMsg settingInfo(HttpSession session,@RequestHeader("Authorization") String token,String name,int gender,String birthday,String code){
        return userService.settingInfo(session,token,name,gender,birthday,code);
    }


    @PostMapping("/updatePassword")
    public ResultMsg updatePassword(HttpSession session,@RequestHeader("Authorization") String token,String oldPassword,String newPassword,String code){
        return userService.settingPassword(session,token,oldPassword,newPassword,code);
    }


    @PostMapping("/updateAvatar")
    public ResultMsg updateAvatar(@RequestHeader("Authorization") String token,String avatar){
        return userService.settingAvatar(token,avatar);
    }

    @PostMapping("/getOldEmailCode")
    public ResultMsg getOldEmailCode(@RequestHeader("Authorization") String token){
        return userService.sendOldEmailCode(token);
    }

    @PostMapping("/getNewEmailCode")
    public ResultMsg getNewEmailCode(String newEmail){
        return userService.sendNewEmailCode(newEmail);
    }

    @PostMapping("/updateEmail")
    public ResultMsg updateEmail(@RequestHeader("Authorization") String token,String newEmail,String oldEmailCode,String newEmailCode){
        return userService.settingEmail(token, newEmail, oldEmailCode, newEmailCode);
    }


    @PostMapping("/findAllSignIn")
    public ResultMsg findAllSignIn(@RequestHeader("Authorization") String token,int page){
        return userService.findAlreadySignIn(token,page);
    }

    @PostMapping("/countAllSignIn")
    public ResultMsg countAllSignIn(@RequestHeader("Authorization") String token){
        return userService.findSignInCount(token);
    }

    @PostMapping("/signIn")
    public ResultMsg signIn(@RequestHeader("Authorization") String token,String msg,String emotion){
        return userService.signIn(token,msg,emotion);
    }

    @PostMapping("/exit")
    public ResultMsg exit(HttpSession session,@RequestHeader("Authorization") String token){
        return userService.exit(session,token);
    }
}
