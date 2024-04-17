package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {
    @Autowired
    private AdminUserService userService;


    @RequestMapping("/adminFindAllUsersForSelect")
    public ResultMsg findAllUsersForSelect() {
        return userService.findAllUsersForSelect();
    }
}
