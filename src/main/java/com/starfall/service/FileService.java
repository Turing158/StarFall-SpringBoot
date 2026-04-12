package com.starfall.service;

import com.starfall.dao.TopicDao;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.TopicFile;
import com.starfall.util.MinioUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Slf4j
public class FileService {

    @Autowired
    MinioUtil minioUtil;
    @Autowired
    TopicDao topicDao;

    public ResultMsg upload(MultipartFile file,String folder ,String fileName){
        return minioUtil.upload(folder,fileName,file) != null ? ResultMsg.success() : ResultMsg.error("ERROR");
    }

    public void getFile(String filename,HttpServletResponse resp) {
        getFile(filename,resp,true);
    }

    public void getFile(String filename,HttpServletResponse resp,boolean defaultContentType) {
        if (filename == null || filename.isBlank()) {
            return;
        }
        try {
            String presignedUrl = minioUtil.preview(filename);
            resp.setHeader("Access-Control-Allow-Credentials", "true");
            URL url = new URL(presignedUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if(defaultContentType){
                resp.setContentType(connection.getContentType());
            }
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
            log.error("Error downloading file: {}", filename, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void getDownloadTopicFile(String fileId,HttpServletResponse resp) {
        TopicFile file = topicDao.findTopicFileById(fileId);
        if(file == null){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        resp.setContentType("application/octet-stream");
        try{
            resp.setHeader("Content-Disposition", "attachment; filename=" + new String(file.getFileName().getBytes(),"ISO8859-1"));
        }
        catch (UnsupportedEncodingException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        getFile("user/" + file.getUser() + "/topic/" + file.getTopicId() + "/file/" + file.getId(),resp,false);
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
