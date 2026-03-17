package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private HomeService homeService;

//    @PostMapping("/publicHomeTalk")
    @PostMapping("/talk/insert")
    public ResultMsg publicHomeTalk(String content,@RequestHeader("Authorization") String token){
        return homeService.publicHomeTalk(content,token);
    }

//    @PostMapping("/findAllHomeTalk")
    @PostMapping("/talk/find")
    public ResultMsg findAllHomeTalk(int num){
        return homeService.findAllHomeTalk(num);
    }

//    @PostMapping("/deleteHomeTalk")
    @PostMapping("/talk/delete")
    public ResultMsg deleteHomeTalk(String date,@RequestHeader("Authorization") String token){
        return homeService.deleteHomeTalk(date,token);
    }

//    @PostMapping("/findAdvertisementByPosition")
    @PostMapping("/ad/find")
    public ResultMsg findAdvertisementByPosition(String position){
        return ResultMsg.success(homeService.findAdvertisementByPosition(position));
    }

    @PostMapping("/notice/find")
    public ResultMsg findAllNotice(){
        return ResultMsg.success(homeService.findAllNotice());
    }
}
