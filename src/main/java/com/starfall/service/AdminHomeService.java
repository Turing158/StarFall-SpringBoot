package com.starfall.service;

import com.starfall.dao.AdminHomeDao;
import com.starfall.entity.Advertisement;
import com.starfall.entity.HomeTalk;
import com.starfall.entity.MultipartFileImpl;
import com.starfall.entity.ResultMsg;
import com.starfall.util.CodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AdminHomeService {
    @Autowired
    private AdminHomeDao adminHomeDao;
    @Autowired
    private FileService fileService;

    public ResultMsg findAllAdvertisement(int page) {
        return ResultMsg.success(adminHomeDao.findAllAdvertisement((page-1)*5),adminHomeDao.countAdvertisement());
    }

    public ResultMsg insertAdvertisement(Advertisement advertisement) {
        advertisement.setId("AD"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS"))+ CodeUtil.getCode(4));
        byte[] bytes = CodeUtil.getBase64Bytes(advertisement.getFile());
        String filename= advertisement.getId()+".png";
        String folder = "advertisement/"+advertisement.getPosition();
        advertisement.setFile(folder+"/"+filename);
        int i = adminHomeDao.insertAdvertisement(advertisement);
        advertisement.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        MultipartFile file = new MultipartFileImpl(bytes,filename);
        fileService.upload(file,folder,filename);
        return i == 1 ? ResultMsg.success() : ResultMsg.error("ERROR");
    }

    public ResultMsg updateAdvertisement(Advertisement advertisement) {
        int i = adminHomeDao.updateAdvertisement(advertisement);
        return i == 1 ? ResultMsg.success() : ResultMsg.error("ERROR");
    }

    public ResultMsg deleteAdvertisement(String id) {
        fileService.removeFile(adminHomeDao.findAdvertisementById(id).getFile());
        int i = adminHomeDao.deleteAdvertisement(id);
        return i == 1 ? ResultMsg.success() : ResultMsg.error("ERROR");
    }


    public ResultMsg findAllHomeTalk(int page,String keyword) {
        keyword = "%" + keyword + "%";
        return ResultMsg.success(adminHomeDao.findAllHomeTalk((page-1)*10,keyword),adminHomeDao.countHomeTalk(keyword));
    }

    public ResultMsg insertHomeTalk(HomeTalk homeTalk) {
        if(homeTalk.getId() == null || homeTalk.getId().isEmpty()){
            homeTalk.setId("HT"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS"))+ CodeUtil.getCode(4));
        }
        int i = adminHomeDao.insertHomeTalk(homeTalk);
        return i == 1 ? ResultMsg.success() : ResultMsg.error("ERROR");
    }

    public ResultMsg deleteHomeTalk(String id) {
        int i = adminHomeDao.deleteHomeTalkById(id);
        return i == 1 ? ResultMsg.success() : ResultMsg.error("ERROR");
    }

    public ResultMsg updateHomeTalk(HomeTalk homeTalk) {
        int i = adminHomeDao.updateHomeTalkById(homeTalk);
        return i == 1 ? ResultMsg.success() : ResultMsg.error("ERROR");
    }

}
