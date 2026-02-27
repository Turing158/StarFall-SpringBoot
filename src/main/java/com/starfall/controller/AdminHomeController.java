package com.starfall.controller;

import com.starfall.entity.Advertisement;
import com.starfall.entity.HomeTalk;
import com.starfall.entity.ResultMsg;
import com.starfall.service.AdminHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/starfall/console/home")
public class AdminHomeController {

    @Autowired
    private AdminHomeService adminHomeService;

    @PostMapping("/adminFindAllAdvertisement")
    public ResultMsg findAllAdvertisement(int page){
        return adminHomeService.findAllAdvertisement(page);
    }

    @PostMapping("/adminInsertAdvertisement")
    public ResultMsg insertAdvertisement(@RequestBody Advertisement advertisement){
        return adminHomeService.insertAdvertisement(advertisement);
    }

    @PostMapping("/adminUpdateAdvertisement")
    public ResultMsg updateAdvertisement(@RequestBody Advertisement advertisement){
        return adminHomeService.updateAdvertisement(advertisement);
    }

    @PostMapping("/adminDeleteAdvertisement")
    public ResultMsg deleteAdvertisement(String id){
        return adminHomeService.deleteAdvertisement(id);
    }

    @PostMapping("/adminFindAllHomeTalk")
    public ResultMsg findAllHomeTalk(int page,String keyword){
        return adminHomeService.findAllHomeTalk(page,keyword);
    }

    @PostMapping("/adminInsertHomeTalk")
    public ResultMsg insertHomeTalk(@RequestBody HomeTalk homeTalk){
        return adminHomeService.insertHomeTalk(homeTalk);
    }

    @PostMapping("/adminDeleteHomeTalk")
    public ResultMsg deleteHomeTalk(String id){
        return adminHomeService.deleteHomeTalk(id);
    }

    @PostMapping("/adminUpdateHomeTalk")
    public ResultMsg updateHomeTalk(@RequestBody HomeTalk homeTalk){
        return adminHomeService.updateHomeTalk(homeTalk);
    }

}
