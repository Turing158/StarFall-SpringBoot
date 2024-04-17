package com.starfall.service;

import com.starfall.dao.AdminNoticeDao;
import com.starfall.entity.Notice;
import com.starfall.entity.ResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminNoticeService {

    @Autowired
    private AdminNoticeDao noticeDao;

    public ResultMsg findAllNotice(int page) {
        List<Notice> notices = noticeDao.findAllNotice((page-1)*10);
        int count = noticeDao.countNotice();
        return ResultMsg.success(notices, count);
    }

    public ResultMsg addNotice(Notice notice) {
        if(noticeDao.existNoticeById(notice.getId()) == 0){
            notice.setId(noticeDao.countNotice()+1);
            int result = noticeDao.addNotice(notice);
            if (result == 1) {
                return ResultMsg.success();
            }
            return ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("EXIST_NOTICE");
    }

    public ResultMsg updateNotice(Notice notice) {
        if(noticeDao.existNoticeById(notice.getId()) == 1){
            int result = noticeDao.updateNotice(notice);
            if (result == 1) {
                return ResultMsg.success();
            }
            return ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("NOT_EXIST_NOTICE");
    }


    public ResultMsg deleteNotice(int id) {
        if(noticeDao.existNoticeById(id) == 1){
            int result = noticeDao.deleteNotice(id);
            if (result == 1) {
                return ResultMsg.success();
            }
            return ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("NOT_EXIST_NOTICE");
    }

}
