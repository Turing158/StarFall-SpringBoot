package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.service.LiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/live")
public class LiveController {

    @Autowired
    private LiveService liveService;

//    @PostMapping("/findLiveBroadcast")
    @PostMapping("/find")
    public ResultMsg findLive(int index,String platform){
        return liveService.findLive(index,platform);
    }

//    @PostMapping("/findAllLiveByUser")
    @PostMapping("/apply/user/find")
    public ResultMsg findAllLiveByUser(@RequestHeader("Authorization") String token, int page){
        return liveService.findAllLiveByUser(token,page);
    }

//    @PostMapping("/appendLiveApply")
    @PostMapping("/apply/insert")
    public ResultMsg appendLiveApply(@RequestHeader("Authorization") String token,String url,String platform){
        return liveService.appendLiveApply(token,url,platform);
    }

//    @PostMapping("/deleteLiveApply")
    @PostMapping("/apply/delete")
    public ResultMsg deleteLiveApply(@RequestHeader("Authorization") String token,String id){
        return liveService.deleteLiveApply(token,id);
    }

//    @PostMapping("/findAllLiveApplyOnAudit")
    @PostMapping("/apply/audit/find")
    public ResultMsg findAllLiveApplyByStatus0(@RequestHeader("Authorization") String token,int page){
        return liveService.findAllLiveApplyByStatus0(token,page);
    }

//    @PostMapping("/updateLiveStatus")
    @PostMapping("/apply/audit/update")
    public ResultMsg updateLiveStatus(@RequestHeader("Authorization") String token,String id,String playUid,String reason,boolean status){
        return liveService.updateLiveStatus(token,id,playUid,reason,status);
    }
}
