package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.entity.UserPersonalized;
import com.starfall.entity.UserPersonalizedDTO;
import com.starfall.service.MedalService;
import com.starfall.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {


    @Autowired
    UserService userService;
    @Autowired
    MedalService medalService;

    @PostMapping("/login")
    public ResultMsg login(String account, String password,String code){
        return ResultMsg.success(userService.login(account,password,code));
    }

//    @PostMapping("/getUserInfo")
    @PostMapping("/info/find")
    public ResultMsg getUserInfo(@RequestHeader("Authorization") String token){
        return userService.getUserInfo(token);
    }

    @PostMapping("/register")
    public ResultMsg register(String user, String password,String email, String emailCode,String code){
        userService.register(user,password,email,emailCode,code);
        return ResultMsg.success();
    }

//    @PostMapping("/getEmailCode")
    @PostMapping("/email/code")
    public ResultMsg getEmailCode(String email,boolean isRegister){
        return userService.getEmailCode(email,isRegister);
    }


//    @PostMapping("/checkForgetPassword")
    @PostMapping("/forget/password/check")
    public ResultMsg checkForgetPassword(String email,String emailCode,String code){
        return userService.checkForgetPassword(email,emailCode,code);
    }

//    @PostMapping("/forgetPassword")
    @PostMapping("/forget/password/update")
    public ResultMsg forgetPassword(String changeToken,String password){
        return userService.forgetPassword(changeToken,password);
    }

//    @PostMapping("/findUserByUser")
    @PostMapping("/info/user/find")
    public ResultMsg findUserByUser(String user){
        return userService.findUserByUser(user);
    }

//    @PostMapping("/updateUserInfo")
    @PostMapping("/info/update")
    public ResultMsg settingInfo(@RequestHeader("Authorization") String token,String name,int gender,String birthday,String code){
        return ResultMsg.success(userService.settingInfo(token,name,gender,birthday,code));
    }


//    @PostMapping("/updatePassword")
    @PostMapping("/password/update")
    public ResultMsg updatePassword(@RequestHeader("Authorization") String token,String oldPassword,String newPassword,String code){
        return userService.settingPassword(token,oldPassword,newPassword,code);
    }


//    @PostMapping("/updateAvatar")
    @PostMapping("/avatar/update")
    public ResultMsg updateAvatar(@RequestHeader("Authorization") String token,String avatarBase64){
        return userService.settingAvatar(token,avatarBase64);
    }

//    @PostMapping("/getOldEmailCode")
    @PostMapping("/email/old/code")
    public ResultMsg getOldEmailCode(@RequestHeader("Authorization") String token){
        return userService.sendOldEmailCode(token);
    }

//    @PostMapping("/getNewEmailCode")
    @PostMapping("/email/new/code")
    public ResultMsg getNewEmailCode(@RequestHeader("Authorization") String token,String newEmail){
        return userService.sendNewEmailCode(token,newEmail);
    }

//    @PostMapping("/updateEmail")
    @PostMapping("/email/update")
    public ResultMsg updateEmail(@RequestHeader("Authorization") String token,String newEmail,String oldEmailCode,String newEmailCode){
        return userService.settingEmail(token, newEmail, oldEmailCode, newEmailCode);
    }


//    @PostMapping("/signIn/findAll")
    @PostMapping("/sign-in/find")
    public ResultMsg findAllSignIn(@RequestHeader("Authorization") String token,int page){
        return userService.findAllSignIn(token,page);
    }

//    @PostMapping("/signIn/check")
    @PostMapping("/sign-in/check")
    public ResultMsg checkSignIn(@RequestHeader("Authorization") String token){
        return userService.checkSignIn(token);
    }

//    @PostMapping("/signIn")
    @PostMapping("/sign-in")
    public ResultMsg signIn(@RequestHeader("Authorization") String token,String msg,String emotion){
        return ResultMsg.success(userService.signIn(token,msg,emotion));
    }

    @PostMapping("/exit")
    public ResultMsg exit(HttpSession session,@RequestHeader("Authorization") String token){
        return userService.exit(session,token);
    }

//    @PostMapping("/isExpire")
    @PostMapping("/login/expire")
    public ResultMsg isExpire(@RequestHeader("Authorization") String token){
        return userService.isExpire(token);
    }

//    @PostMapping("/toAdmin")
    @PostMapping("/permission/admin")
    public ResultMsg toAdmin(@RequestHeader("Authorization") String token){
        return userService.toAdmin(token);
    }

    @PostMapping("/personalized/find")
    public ResultMsg findPersonalized(@RequestHeader("Authorization") String token){
        UserPersonalized userPersonalized = userService.findPersonalized(token);
        return ResultMsg.success(userPersonalized);
    }

    @PostMapping("/personalized/update")
    public ResultMsg updatePersonalized(@RequestHeader("Authorization") String token, @RequestBody UserPersonalizedDTO personalized){
        userService.updatePersonalized(token,personalized);
        return ResultMsg.success();
    }

    @PostMapping("/signature/update")
    public ResultMsg updatePersonalized(@RequestHeader("Authorization") String token, String signature ,String code){
        userService.updateSignature(token,signature,code);
        return ResultMsg.success();
    }

//    @PostMapping("/medal/menu")
    @PostMapping("/medal/menu/find")
    public ResultMsg findMedalOnMenu(@RequestHeader("Authorization") String token) {
        return ResultMsg.success(medalService.findUserMedalOnMenu(token));
    }

//    @PostMapping("/medal/person")
    @PostMapping("/medal/person/find")
    public ResultMsg findUserMedal(String user) {
        return ResultMsg.success(medalService.findUserMedal(user));
    }

//    @PostMapping("/medal/all")
    @PostMapping("/medals/find")
    public ResultMsg findAllMedal(String user,int page) {
        return ResultMsg.success(medalService.findAllMedal(user, page));
    }

    @PostMapping("/medal/find")
    public ResultMsg findMedal(String id) {
        return ResultMsg.success(medalService.findMedalById(id));
    }


    @PostMapping("/minecraft/code/get")
    public ResultMsg getDeviceCode(@RequestHeader("Authorization") String token) {
        return ResultMsg.success(userService.getDeviceCode(token));
    }

    @PostMapping("/minecraft/code/verify")
    public ResultMsg getMicrosoftToken(@RequestHeader("Authorization") String token,String deviceCode) {
        return ResultMsg.success(userService.getMicrosoftToken(token,deviceCode));
    }

    @PostMapping("/minecraft/verify")
    public ResultMsg minecraftVerify(String minecraftToken,@RequestHeader("Authorization") String token) {
        String info = userService.minecraftVerify(minecraftToken,token);
        return ResultMsg.success(info);
    }
}
