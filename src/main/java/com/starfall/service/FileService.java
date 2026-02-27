package com.starfall.service;

import com.starfall.dao.TopicDao;
import com.starfall.entity.FileStore;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.TopicFile;
import com.starfall.util.JwtUtil;
import com.starfall.util.MinioUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FileService {

    @Autowired
    MinioUtil minioUtil;
    @Autowired
    TopicDao topicDao;

    public ResultMsg upload(MultipartFile file,String folder ,String fileName){
        return minioUtil.upload(folder,fileName,file) != null ? ResultMsg.success() : ResultMsg.error("ERROR");
    }

    public void getFile(String filename,HttpServletResponse resp) {
        if (filename == null || filename.isBlank()) {
            return;
        }
        try {
            String presignedUrl = minioUtil.preview(filename);
            resp.setHeader("Access-Control-Allow-Credentials", "true");
            URL url = new URL(presignedUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            resp.setContentType(connection.getContentType());
            resp.setContentLength(connection.getContentLength());
            try (InputStream inputStream = connection.getInputStream();
                 OutputStream outputStream = resp.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void getDownloadTopicFile(String fileId,HttpServletResponse resp) {
        TopicFile file = topicDao.findTopicFileById(fileId);
        if(file == null){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try{
            resp.setHeader("Content-Disposition", "attachment; filename=" + new String(file.getFileName().getBytes(),"ISO8859-1"));
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        getFile("user/" + file.getUser() + "/topic/" + file.getTopicId() + "/file/" + file.getId(),resp);
    }

    public ResultMsg removeFile(String filename){
        return minioUtil.remove(filename) ? ResultMsg.success("SUCCESS") : ResultMsg.error("ERROR");
    }

    public ResultMsg removeFolder(String folder){
        return minioUtil.deleteFolder(folder) ? ResultMsg.success("SUCCESS") : ResultMsg.error("ERROR");
    }

    public ResultMsg move(String oldPath,String newPath){
        return minioUtil.moveFolder(oldPath,newPath) ? ResultMsg.success("SUCCESS") : ResultMsg.error("ERROR");
    }

}
