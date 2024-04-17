package com.starfall.dao;

import com.starfall.entity.Notice;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminNoticeDao {

    @Select("select * from starfall.notice order by id desc limit #{page},10")
    List<Notice> findAllNotice(int page);


    @Select("select count(*) from starfall.notice")
    int countNotice();

    @Select("select count(*) from starfall.notice where id=#{id}")
    int existNoticeById(int id);

    @Insert("insert into starfall.notice value (#{id},#{content})")
    int addNotice(Notice notice);

    @Update("update starfall.notice set content=#{content} where id=#{id}")
    int updateNotice(Notice notice);

    @Delete("delete from starfall.notice where id=#{id}")
    int deleteNotice(int id);
}
