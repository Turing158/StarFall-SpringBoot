package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.service.AdminOtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/starfall/console/console/other")
public class AdminOtherController {
    @Autowired
    private AdminOtherService adminOtherService;


}
