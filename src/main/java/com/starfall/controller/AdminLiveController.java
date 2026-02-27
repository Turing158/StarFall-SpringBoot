package com.starfall.controller;

import com.starfall.entity.LiveBroadcast;
import com.starfall.entity.ResultMsg;
import com.starfall.service.AdminLiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/starfall/console/live")
public class AdminLiveController {

    @Autowired
    private AdminLiveService adminLiveService;

    @PostMapping("/adminFindAll")
    public ResultMsg findAllLive(int page){
        return adminLiveService.findAllLive(page);
    }

    @PostMapping("/adminAppend")
    public ResultMsg insertLive(@RequestBody LiveBroadcast live){
        return adminLiveService.insertLive(live);
    }

    @PostMapping("/adminUpdate")
    public ResultMsg updateLive(@RequestBody LiveBroadcast live){
        return adminLiveService.updateLive(live);
    }

    @PostMapping("/adminDelete")
    public ResultMsg deleteLive(String id){
        return adminLiveService.deleteLive(id);
    }
}
