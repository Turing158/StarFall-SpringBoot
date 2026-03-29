package com.starfall.dao.admin;

import com.starfall.entity.Advertisement;
import com.starfall.entity.FileStore;
import com.starfall.entity.HomeTalk;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminHomeDao {
    @Select("select * from starfall.advertisement order by position asc, sequence asc limit #{num},5")
    List<Advertisement> findAllAdvertisement(int num);

    @Select("select count(*) from starfall.advertisement")
    int countAdvertisement();

    @Select("select * from starfall.advertisement where id=#{id}")
    Advertisement findAdvertisementById(String id);

    @Insert("insert into starfall.advertisement values(#{id},#{title},#{file},#{date},#{link},#{position},#{sequence})")
    int insertAdvertisement(Advertisement advertisement);

    @Delete("delete from starfall.advertisement where id=#{id}")
    int deleteAdvertisement(String id);

    @Update("update starfall.advertisement set title=#{title},link=#{link},date=#{date},sequence=#{sequence} where id=#{id}")
    int updateAdvertisement(Advertisement advertisement);

    @Select("select * from starfall.home_talk h join starfall.user u on h.user=u.user where h.content like #{keyword} or u.name like #{keyword} or h.user like #{keyword} order by date desc limit #{num},10")
    List<HomeTalk> findAllHomeTalk(int num,String keyword);

    @Select("select count(*) from starfall.home_talk h join starfall.user u on h.user=u.user where h.content like #{keyword} or u.name like #{keyword} or h.user like #{keyword}")
    int countHomeTalk(String keyword);

    @Select("select * from starfall.home_talk where user=#{user} and date=#{date}")
    HomeTalk findHomeTalkByUserAndDate(String user,String date);

    @Insert("insert into starfall.home_talk values(#{id},#{user},#{content},#{date})")
    int insertHomeTalk(HomeTalk homeTalk);

    @Delete("delete from starfall.home_talk where id=#{id}")
    int deleteHomeTalkById(String id);

    @Delete("delete from starfall.home_talk where user=#{user}")
    int deleteHomeTalkByUser(String user);

    @Update("update starfall.home_talk set content=#{content},user=#{user},date=#{date} where id=#{id}")
    int updateHomeTalkById(HomeTalk homeTalk);

}
