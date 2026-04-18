package com.starfall.controller.admin;

import com.starfall.entity.ResultMsg;
import com.starfall.service.admin.AdminOtherService;
import com.starfall.util.CodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;

@RestController
@RequestMapping("/starfall/console/other")
public class AdminOtherController {
    @Autowired
    private AdminOtherService adminOtherService;

    @GetMapping("/redis/status")
    public ResultMsg findRedisStatus(){
        return ResultMsg.success(adminOtherService.findRedisStatus());
    }

    @GetMapping("/nacos/status")
    public ResultMsg findNacosStatus(){
        return ResultMsg.success(adminOtherService.findNacosFullStatus());
    }

    @GetMapping("/minio/status")
    public ResultMsg findMinioStatus(){
        return ResultMsg.success(adminOtherService.findMinioFullStatus());
    }

    @PostMapping("/minio/buckets")
    public ResultMsg findMinioBucketStatus(String bucketName, String startAfter,int page, int pageSize){
        return ResultMsg.success(adminOtherService.listObjectsByPage(bucketName, startAfter, page, pageSize));
    }

    @PostMapping("/uploadMedalImg")
    public ResultMsg uploadMedalImg(String name, String base64){
        return ResultMsg.success(adminOtherService.uploadMedalImg(name, base64));
    }

    @GetMapping("/getMedalImg")
    public ResultMsg getMedalImg(){
        return ResultMsg.success(adminOtherService.getMedalImg());
    }
}
