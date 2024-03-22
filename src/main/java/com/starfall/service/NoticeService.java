package com.starfall.service;

import com.starfall.dao.NoticeDao;
import com.starfall.entity.ResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoticeService {
    @Autowired
    NoticeDao noticeDao;

    public ResultMsg findAllNotice(){
        return new ResultMsg("SUCCESS",noticeDao.findAllNotice());
    }
}
