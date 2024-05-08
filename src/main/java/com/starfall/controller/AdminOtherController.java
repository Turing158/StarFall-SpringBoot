package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.service.AdminOtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/other")
public class AdminOtherController {
    @Autowired
    private AdminOtherService adminOtherService;

    @PostMapping("/findAllTopImgInfo")
    public ResultMsg findAllTopImgInfo (){
        return adminOtherService.findAllTopImgInfo();
    }

    @PostMapping("/uploadTopImg")
    public ResultMsg uploadTopImg (String imgBase64){
        return adminOtherService.uploadTopImg(imgBase64);
    }

    @PostMapping("/upMoveTopImg")
    public ResultMsg upMoveTopImg (String imgName){
        return adminOtherService.upMoveTopImg(imgName);
    }

    @PostMapping("/downMoveTopImg")
    public ResultMsg downMoveTopImg (String imgName){
        return adminOtherService.downMoveTopImg(imgName);
    }

    @PostMapping("/deleteTopImg")
    public ResultMsg deleteTopImg (String imgName){
        return adminOtherService.deleteTopImg(imgName);
    }

    @PostMapping("/findAllAdImgInfo")
    public ResultMsg findAllAdImgInfo (){
        return adminOtherService.findAllAdImgInfo();
    }

    @PostMapping("/uploadAdImg")
    public ResultMsg uploadAdImg (String imgBase64){
        return adminOtherService.uploadAdImg(imgBase64);
    }

    @PostMapping("/upMoveAdImg")
public ResultMsg upMoveAdImg (String imgName){
        return adminOtherService.upMoveAdImg(imgName);
    }

    @PostMapping("/downMoveAdImg")
    public ResultMsg downMoveAdImg (String imgName){
        return adminOtherService.downMoveAdImg(imgName);
    }

    @PostMapping("/deleteAdImg")
    public ResultMsg deleteAdImg (String imgName){
        return adminOtherService.deleteAdImg(imgName);
    }


}
