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
        return ResultMsg.success(liveService.findLive(index,platform));
    }

//    @PostMapping("/findAllLiveByUser")
    @PostMapping("/apply/user/find")
    public ResultMsg findAllLiveByUser(@RequestHeader("Authorization") String token, int page){
        var r = liveService.findAllLiveByUser(token,page);
        return ResultMsg.success(r.getFirst(),r.getSecond());
    }

//    @PostMapping("/appendLiveApply")
    @PostMapping("/apply/insert")
    public ResultMsg appendLiveApply(@RequestHeader("Authorization") String token,String url,String platform){
        liveService.appendLiveApply(token,url,platform);
        return ResultMsg.success();
    }

//    @PostMapping("/deleteLiveApply")
    @PostMapping("/apply/delete")
    public ResultMsg deleteLiveApply(@RequestHeader("Authorization") String token,String id){
        liveService.deleteLiveApply(token,id);
        return ResultMsg.success();
    }

//    @PostMapping("/findAllLiveApplyOnAudit")
    @PostMapping("/apply/audit/find")
    public ResultMsg findAllLiveApplyByStatus0(@RequestHeader("Authorization") String token,int page){
        var r = liveService.findAllLiveApplyByStatus0(token,page);
        return ResultMsg.success(r.getFirst(),r.getSecond());
    }

//    @PostMapping("/updateLiveStatus")
    @PostMapping("/apply/audit/update")
    public ResultMsg updateLiveStatus(@RequestHeader("Authorization") String token,String id,String playUid,String reason,boolean status){
        liveService.updateLiveStatus(token,id,playUid,reason,status);
        return ResultMsg.success();
    }
}
