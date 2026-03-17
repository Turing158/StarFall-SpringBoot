package com.starfall.dao;

import com.starfall.entity.Medal;
import com.starfall.entity.MedalMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MedalDao {

    @Select("select * from starfall.medal where id = #{id}")
    Medal findById(String id);

    @Select("select count(*) from starfall.medal_mapper where user = #{user} and medal = #{medal}")
    int countByUserAndMedal(String user, String medal);

    @Select("select * from starfall.medal_mapper mm " +
            "left join starfall.medal m on m.id = mm.medal " +
            "where mm.user = #{user} and (mm.expire_time is null or mm.expire_time > now()) " +
            "order by mm.gain_time desc limit #{offset},#{size}")
    List<MedalMapper> findAllByUserLimit(String user,long offset,int size);

    @Select("select * from starfall.medal m " +
            "left join starfall.medal_mapper mm on m.id = mm.medal and mm.user = #{user} " +
            "order by " +
            "case when mm.gain_time is not null then 0 else 1 end, " +
            "mm.gain_time desc ," +
            "mm.expire_time desc ," +
            "m.create_time desc limit #{index},20")
    List<MedalMapper> findAllMedal(String user,int index);

    //判断用户是否已经注册超过三年
    @Select("select count(*) from starfall.user where user = #{user} and create_time <= DATE_SUB(CURDATE(), interval 3 year);")
    int countUserAlready3Year(String user);

    @Insert("insert into starfall.medal_mapper (user,medal,gain_time,expire_time) values (#{user},#{id},#{gainTime},#{expireTime})")
    void insertMedalMapper(MedalMapper medalMapper);

    @Update("update starfall.medal_mapper set gain_time = #{gainTimer} ,expire_time = #{expireTime} where user = #{user} and medal = #{medal}")
    void updateMedalMapperGainTimeAndExpireTime(MedalMapper medalMapper);
}
