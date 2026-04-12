package com.starfall.controller;

import com.starfall.Exception.NotLoginException;
import com.starfall.Exception.PermissionException;
import com.starfall.entity.ResultMsg;
import com.starfall.service.FileService;
import com.starfall.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {
    @Autowired
    FileService fileService;
    @Autowired
    JwtUtil jwtUtil;

//    @PostMapping("/upload")
    public ResultMsg upload(@RequestParam("file") MultipartFile file,String user,String usage) {
        return fileService.upload(file, user, usage);
    }

    @GetMapping("/url/origin/**")
    public void getFile(HttpServletRequest req,HttpServletResponse resp) {
        String path = req.getRequestURI();
        String filename = path.substring(path.indexOf("/url/origin") + 11);
        if(filename.isEmpty() || filename.equals("/") || filename.equals("/undefined")){
            return;
        }
        if(filename.contains("/topic/") && filename.contains("/file/")){
            throw new PermissionException("NOT_PERMISSION","没有权限访问该文件");
        }
        fileService.getFile(filename, resp);
    }

    @GetMapping("/url/txt/**")
    public void getTextFile(HttpServletRequest req,HttpServletResponse resp){
        String path = req.getRequestURI();
        String filename = path.substring(path.indexOf("/url/txt/") + 9);
        resp.setContentType("text/plain");
        fileService.getFile(filename, resp);
    }

    @GetMapping("/url/download/**")
    public void getDownloadFile(HttpServletRequest req,HttpServletResponse resp,@RequestHeader("Authorization") String token){
        Claims claims;
        if(token == null || !token.startsWith("Bearer ")){
            log.info("【下载附件】Authorization header为空，未登录，拒绝访问");
            throw new NotLoginException("NOT_LOGIN");
        }
        try {
            claims = jwtUtil.parseToken(token);
        } catch (Exception e) {
            log.info("【下载附件】token解析失败，未登录,拒绝访问");
            throw new NotLoginException("NOT_LOGIN");
        }
        String path = req.getRequestURI();
        String fileId = path.substring(path.indexOf("/url/download/") + 14);
        log.info("【下载附件】用户 {} 下载附件：{}",claims.get("USER"),fileId);
        fileService.getDownloadTopicFile(fileId, resp);
    }


}
