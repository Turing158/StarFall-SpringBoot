package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {


    @Autowired
    UserService userService;
    @PostMapping("/login")
    public ResultMsg login(String account, String password,String code){
        return userService.login(account,password,code);
    }

    @PostMapping("/getUserInfo")
    public ResultMsg getUserInfo(@RequestHeader("Authorization") String token){
        return userService.getUserInfo(token);
    }

    @PostMapping("/register")
    public ResultMsg register(String user, String password,String email, String emailCode,String code){
        return userService.register(user,password,email,emailCode,code);
    }
    @PostMapping("/getEmailCode")
    public ResultMsg getEmailCode(String email,boolean isRegister){
        return userService.getEmailCode(email,isRegister);
    }


    @PostMapping("/checkForgetPassword")
    public ResultMsg checkForgetPassword(String email,String emailCode,String code){
        return userService.checkForgetPassword(email,emailCode,code);
    }

    @PostMapping("/forgetPassword")
    public ResultMsg forgetPassword(String changeToken,String password){
        return userService.forgetPassword(changeToken,password);
    }

    @PostMapping("/findUserByUser")
    public ResultMsg findUserByUser(String user){
        return userService.findUserByUser(user);
    }


    @PostMapping("/updateUserInfo")
    public ResultMsg settingInfo(@RequestHeader("Authorization") String token,String name,int gender,String birthday,String code){
        return userService.settingInfo(token,name,gender,birthday,code);
    }


    @PostMapping("/updatePassword")
    public ResultMsg updatePassword(@RequestHeader("Authorization") String token,String oldPassword,String newPassword,String code){
        return userService.settingPassword(token,oldPassword,newPassword,code);
    }


    @PostMapping("/updateAvatar")
    public ResultMsg updateAvatar(@RequestHeader("Authorization") String token,String avatarBase64){
        return userService.settingAvatar(token,avatarBase64);
    }

    @PostMapping("/getOldEmailCode")
    public ResultMsg getOldEmailCode(@RequestHeader("Authorization") String token){
        return userService.sendOldEmailCode(token);
    }

    @PostMapping("/getNewEmailCode")
    public ResultMsg getNewEmailCode(@RequestHeader("Authorization") String token,String newEmail){
        return userService.sendNewEmailCode(token,newEmail);
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

    @PostMapping("/isExpire")
    public ResultMsg isExpire(@RequestHeader("Authorization") String token){
        return userService.isExpire(token);
    }

    @PostMapping("/toAdmin")
    public ResultMsg toAdmin(@RequestHeader("Authorization") String token){
        return userService.toAdmin(token);
    }
}
