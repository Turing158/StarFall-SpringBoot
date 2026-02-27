package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    FileService fileService;

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
    public void getDownloadFile(HttpServletRequest req,HttpServletResponse resp){
        String path = req.getRequestURI();
        String fileId = path.substring(path.indexOf("/url/download/") + 14);
        resp.setContentType("application/force-download");
        fileService.getDownloadTopicFile(fileId, resp);
    }


}
