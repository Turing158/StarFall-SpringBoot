package com.starfall.controller.admin;

import com.starfall.entity.*;
import com.starfall.service.admin.AdminUserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/starfall/console/user")
public class AdminUserController {
    @Autowired
    private AdminUserService userService;


    @PostMapping("/adminFindAllUsersForSelect")
    public ResultMsg findAllUsersForSelect(String keyword) {
        return userService.findAllUsersForSelect(keyword);
    }

    @PostMapping("/adminFindAllUsers")
    public ResultMsg findAllUsers(int page,String keyword) {
        return userService.findAllUsers(page,keyword);
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
    public ResultMsg findAllSignIn(int page,String keyword) {
        return userService.findAllSignIn(page,keyword);
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


    @PostMapping("/admin/personalized/find")
    public ResultMsg findAllPersonalized(int page) {
        log.info("查询用户个性化设置，page={}", page);
        var pair = userService.findAllPersonalized(page);
        return ResultMsg.success(pair.getFirst(),pair.getSecond());
    }

    @PostMapping("/admin/personalized/update")
    public ResultMsg updatePersonalized(@RequestBody UserPersonalized userPersonalized){
        log.info("更新用户个性化设置，userPersonalized={}", userPersonalized);
        userService.updatePersonalized(userPersonalized);
        return ResultMsg.success();
    }

    @PostMapping("/admin/medalMapper/find")
    public ResultMsg findAllMedalMapper(int page) {
        log.info("查询用户勋章映射，page={}", page);
        var pair = userService.findAllMedalMapper(page);
        return ResultMsg.success(pair.getFirst(),pair.getSecond());
    }

     @PostMapping("/admin/medalMapper/insert")
    public ResultMsg insertMedalMapper(@RequestBody MedalMapper medalMapper) {
        log.info("插入用户勋章映射，medalMapper={}", medalMapper);
        userService.insertMedalMapper(medalMapper);
        return ResultMsg.success();
    }

    @PostMapping("/admin/medalMapper/update")
    public ResultMsg updateMedalMapper(@RequestBody MedalMapper medalMapper) {
        log.info("更新用户勋章映射，medalMapper={}", medalMapper);
        userService.updateMedalMapper(medalMapper);
        return ResultMsg.success();
    }

    @PostMapping("/admin/medalMapper/delete")
    public ResultMsg deleteMedalMapper(String user,String id) {
        log.info("删除用户勋章映射，user={}, id={}", user, id);
        userService.deleteMedalMapper(user, id);
        return ResultMsg.success();
    }

    @PostMapping("/admin/medal/find")
    public ResultMsg findAllMedal(int page) {
        log.info("查询用户勋章，page={}", page);
        var pair = userService.findAllMedal(page);
        return ResultMsg.success(pair.getFirst(), pair.getSecond());
    }

    @PostMapping("/admin/medal/insert")
    public ResultMsg insertMedal(@RequestBody Medal medal) {
        log.info("插入用户勋章，medal={}", medal);
        userService.insertMedal(medal);
        return ResultMsg.success();
    }

    @PostMapping("/admin/medal/update")
    public ResultMsg updateMedal(@RequestBody Medal medal) {
        log.info("更新用户勋章，medal={}", medal);
        userService.updateMedal(medal);
        return ResultMsg.success();
    }

    @PostMapping("/admin/medal/delete")
    public ResultMsg deleteMedal(String id) {
        log.info("删除用户勋章，id={}", id);
        userService.deleteMedal(id);
        return ResultMsg.success();
    }
}

