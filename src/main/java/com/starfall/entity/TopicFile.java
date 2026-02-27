package com.starfall.entity;

import com.starfall.util.CodeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicFile {
    String id;
    String user;
    String topicId;
    String uploadDate;
    String fileName;
    String fileLabel;
    long fileSize;

    public static TopicFile parseTopicFileObj(String fileName,String topicId, String fileLabel, String user,String fileBase64){
        TopicFile topicFile = new TopicFile();
        LocalDateTime ldt = LocalDateTime.now();
        topicFile.setId("tf" + ldt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS")) + CodeUtil.getCode(6));
        if(fileName.isEmpty()){
            topicFile.setFileName("附件_"+CodeUtil.getCode(4));
        }
        else if(fileName.length() > 25){
            String tmpFileName = "";
            if(fileName.contains(".")){
                String[] fileNameSplit = fileName.split("\\.");
                int maxFileNameLength = 24 - fileNameSplit[fileNameSplit.length - 1].length();// 24 = 25 -1 (dot)
                if(fileNameSplit.length > 2){
                    for (int i = 0; i < fileNameSplit.length - 2; i++){
                        tmpFileName += fileNameSplit[i];
                    }
                }
                else{
                    tmpFileName += fileNameSplit[0];
                }
                tmpFileName = tmpFileName.substring(0, maxFileNameLength) + "." + fileNameSplit[fileNameSplit.length - 1];
            }
            else{
                tmpFileName = fileName.substring(0,25);
            }
            topicFile.setFileName(tmpFileName);
        }
        else {
            topicFile.setFileName(fileName);
        }
        topicFile.setTopicId(topicId);
        topicFile.setFileLabel(fileLabel);
        topicFile.setUser(user);
        topicFile.setFileSize(fileBase64.length()* 3L /4);
        topicFile.setUploadDate(ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return topicFile;
    }
}
