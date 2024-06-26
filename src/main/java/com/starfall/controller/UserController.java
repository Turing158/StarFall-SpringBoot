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


    @PostMapping("/checkForgetPassword")
    public ResultMsg checkForgetPassword(HttpSession session,String email,String emailCode,String code){
        return userService.checkForgetPassword(session,email,emailCode,code);
    }

    @PostMapping("/forgetPassword")
    public ResultMsg forgetPassword(HttpSession session,@RequestHeader("Authorization") String token,String password){
        return userService.forgetPassword(session,token,password);
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
    public ResultMsg updateAvatar(@RequestHeader("Authorization") String token,String avatarBase64){
        return userService.settingAvatar(token,avatarBase64);
    }

    @PostMapping("/getOldEmailCode")
    public ResultMsg getOldEmailCode(HttpSession session,@RequestHeader("Authorization") String token){
        return userService.sendOldEmailCode(session,token);
    }

    @PostMapping("/getNewEmailCode")
    public ResultMsg getNewEmailCode(HttpSession session,String newEmail){
        return userService.sendNewEmailCode(session,newEmail);
    }

    @PostMapping("/updateEmail")
    public ResultMsg updateEmail(HttpSession session,@RequestHeader("Authorization") String token,String newEmail,String oldEmailCode,String newEmailCode){
        return userService.settingEmail(session,token, newEmail, oldEmailCode, newEmailCode);
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
