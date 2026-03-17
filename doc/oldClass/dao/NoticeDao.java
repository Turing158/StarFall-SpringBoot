package com.starfall.dao;

import com.starfall.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NoticeDao {

    @Select("select * from starfall.notice")
    List<Notice> findAllNotice();
}
