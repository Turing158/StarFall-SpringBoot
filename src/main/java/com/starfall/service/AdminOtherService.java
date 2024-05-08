package com.starfall.service;

import com.starfall.entity.ResultMsg;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

@Service
public class AdminOtherService {

    @Value("${top.img.save.path}")
    String topImgPath = "";

    public ResultMsg findAllTopImgInfo(){
        return findAllImages(topImgPath);
    }

    public ResultMsg uploadTopImg(String imgBase64){
        uploadImage("top",imgBase64, topImgPath);
        return ResultMsg.success(findAllTopImgInfo().getObject());
    }

    public ResultMsg upMoveTopImg(String imgName){
        return upMoveImage("top", imgName, topImgPath);
    }

    public ResultMsg downMoveTopImg(String imgName){
        return downMoveImage("top", imgName, topImgPath);
    }

    public ResultMsg deleteTopImg(String imgName){
        return deleteImage("top",imgName, topImgPath);
    }


    @Value("${topic.ad.img.save.path}")
    String adImgPath = "";
    public ResultMsg findAllAdImgInfo(){
        return findAllImages(adImgPath);
    }

    public ResultMsg uploadAdImg(String imgBase64){
        uploadImage("ad",imgBase64, adImgPath);
        return ResultMsg.success(findAllAdImgInfo().getObject());
    }

    public ResultMsg upMoveAdImg(String imgName){
        return upMoveImage("ad", imgName, adImgPath);
    }

    public ResultMsg downMoveAdImg(String imgName){
        return downMoveImage("ad", imgName, adImgPath);
    }

    public ResultMsg deleteAdImg(String imgName){
        return deleteImage("ad",imgName, adImgPath);
    }



    public ResultMsg findAllImages(String path){
        File[] files = new File(path).listFiles();
        if (files != null) {
            String[] fileNames = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                fileNames[i] = files[i].getName();
            }
            return ResultMsg.success(fileNames);
        }
        return ResultMsg.error("NULL");
    }



    public void uploadImage(String TopOrAd,String base64,String path){
        String avatarOutHead = "data:image/png;base64,";
        if(base64.startsWith(avatarOutHead)){
            base64 = base64.substring(avatarOutHead.length());
        }

        byte[] bytes = Base64.getDecoder().decode(base64);
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {// 调整异常数据
                bytes[i] += 256;
            }
        }
        File[] files = new File(path).listFiles();
        String imgName = "top" + (files.length+1) + ".png";
        try {
            OutputStream out = new FileOutputStream(path + "/" + imgName );
            out.write(bytes);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ResultMsg upMoveImage(String TopOrAd,String imgName,String path){
        File setImg = new File(path + "\\" + imgName);
        if (setImg.exists()) {
            String upImgName = TopOrAd+(Integer.parseInt(imgName.substring(TopOrAd.equals("top")?3:2, imgName.lastIndexOf(".")))-1)+".png";
            File upImg = new File(path + "\\" + upImgName);
            if (upImg.exists()) {
                setImg.renameTo(new File(path + "\\" + "temp.png"));
                upImg.renameTo(new File(path + "\\" + imgName));
                new File(path + "\\" + "temp.png").renameTo(new File(path + "\\" + upImgName));
                return ResultMsg.success();
            }
            return ResultMsg.error("NULL");
        }
        return ResultMsg.error("NULL");
    }

    public ResultMsg downMoveImage(String TopOrAd,String imgName,String path){
        File setImg = new File(path + "\\" + imgName);
        if (setImg.exists()) {
            String downImgName = TopOrAd+(Integer.parseInt(imgName.substring(TopOrAd.equals("top")?3:2, imgName.lastIndexOf(".")))+1)+".png";
            File upImg = new File(path + "\\" + downImgName);
            if (upImg.exists()) {
                setImg.renameTo(new File(path + "\\" + "temp.png"));
                upImg.renameTo(new File(path + "\\" + imgName));
                new File(path + "\\" + "temp.png").renameTo(new File(path + "\\" + downImgName));
                return ResultMsg.success();
            }
            return ResultMsg.error("NULL");
        }
        return ResultMsg.error("NULL");
    }

    public ResultMsg deleteImage(String TopOrAd,String imgName,String path){
        File deleteImg = new File(path + "\\" + imgName);
        int imgIndex = Integer.parseInt(imgName.substring(TopOrAd.equals("top")?3:2, imgName.lastIndexOf(".")));
        if (deleteImg.exists()) {
            deleteImg.delete();
            File[] files = new File(path).listFiles();
            if(imgIndex != files.length){
                for (int i = imgIndex; i < files.length+1; i++) {
                    File file = new File(path + "\\" + TopOrAd + (i+1) + ".png");
                    file.renameTo(new File(path + "\\" + TopOrAd + i + ".png"));
                }
            }
            return ResultMsg.success();
        }
        return ResultMsg.error("NULL");
    }

}
