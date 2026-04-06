package com.starfall.dao;

import com.starfall.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HomeDao {
    @Select("select * from starfall.home_talk join starfall.user u on home_talk.user = u.user order by date desc limit #{num},#{limit}")
    List<HomeTalk> findAllHomeTalk(int num,int limit);

    @Select("select * from starfall.home_talk where user=#{user} and date = #{date}")
    HomeTalk findHomeTalkByUserAndDate(String user,String date);

    @Select("select * from starfall.home_talk h join starfall.user u on h.user = u.user where h.user=#{user} order by date desc limit 0,1")
    HomeTalk findHomeTalk(String user);

    @Select("select count(*) from starfall.home_talk limit 1")
    int countHomeTalk();

    @Insert("insert into starfall.home_talk values(#{id},#{user},#{content},#{date})")
    int insertHomeTalk(HomeTalk homeTalk);

    @Delete("delete from starfall.home_talk where id=#{id}")
    int deleteHomeTalk(String id);

    @Select("select * from starfall.advertisement where position=#{position} order by sequence")
    List<Advertisement> findAdvertisementByPosition(String position);

    @Select("select * from starfall.notice")
    List<Notice> findAllNotice();
}
