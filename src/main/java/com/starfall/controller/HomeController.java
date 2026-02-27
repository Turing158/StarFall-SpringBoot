package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private HomeService homeService;

    @PostMapping("/publicHomeTalk")
    public ResultMsg publicHomeTalk(String content,@RequestHeader("Authorization") String token){
        return homeService.publicHomeTalk(content,token);
    }

    @PostMapping("/findAllHomeTalk")
    public ResultMsg findAllHomeTalk(int num){
        return homeService.findAllHomeTalk(num);
    }

    @PostMapping("/deleteHomeTalk")
    public ResultMsg deleteHomeTalk(String date,@RequestHeader("Authorization") String token){
        return homeService.deleteHomeTalk(date,token);
    }

    @PostMapping("/findAdvertisementByPosition")
    public ResultMsg findAdvertisementByPosition(String position){
        return homeService.findAdvertisementByPosition(position);
    }
}
