package com.starfall.controller;

import com.starfall.Exception.ParamException;
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
        homeService.publicHomeTalk(content,token);
        return ResultMsg.success();
    }

//    @PostMapping("/findAllHomeTalk")
    @PostMapping("/talk/find")
    public ResultMsg findAllHomeTalk(int num){
        if(num < 0){
            throw new ParamException("PARAM_ERROR","num必须是非负数");
        }
        return ResultMsg.success(homeService.findAllHomeTalk(num));
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
