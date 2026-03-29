package com.starfall.dao;

import com.starfall.entity.User;
import com.starfall.entity.UserOtherVO;
import com.starfall.entity.UserPersonalized;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserDao {
    @Select("select count(*) from starfall.user where user = #{user}")
    int existUser(String user);

    @Select("select count(*) from starfall.user where email = #{email}")
    int existEmail(String email);

    @Select("select * from starfall.user where user = #{account} or email = #{account}")
    User findByUserOrEmail(String account);

    @Select("select * from starfall.user_personalized where user = #{user}")
    UserPersonalized findPersonalizedByUser(String user);

    @Select("select online_uuid from starfall.user_personalized where user = #{user}")
    String findOnlineUuidByUser(String user);

    @Select("select user from starfall.user_personalized where online_uuid = #{onlineUUID}")
    String findUserByOnlineUUID(String onlineUUID);

    @Insert("insert into starfall.user (user, password,name,gender,email,birthday,exp,level,avatar,role,create_time,update_time) " +
            "values (#{user},#{password},#{name},#{gender},#{email},#{birthday},#{exp},#{level},#{avatar},#{role},#{createTime},#{updateTime})")
    int insertUser(User user);

    @Insert("insert into starfall.user_personalized (user,signature,online_name,show_online_name,show_collection,show_birthday,show_gender,show_email,create_time,update_time)" +
            "value (#{user},#{signature},#{onlineName},#{showOnlineName},#{showCollection},#{showBirthday},#{showGender},#{showEmail},#{createTime},#{updateTime})")
    int insertPersonalized(UserPersonalized userPersonalized);

    @Update("update starfall.user set exp=#{exp},level=#{level},update_time=#{updateTime} where user=#{user}")
    int updateExp(String user,int exp,int level,String updateTime);

    @Update("update starfall.user set avatar=#{avatar},update_time=#{updateTime} where user=#{user}")
    int updateAvatar(String user,String avatar,String updateTime);

    @Update("update starfall.user set name=#{name},gender=#{gender},birthday=#{birthday},update_time=#{updateTime} where user=#{user}")
    int updateInfo(String user,String name,int gender,String birthday,String updateTime);

    @Update("update starfall.user set password=#{password},update_time=#{updateTime} where user=#{user}")
    int updatePassword(String user,String password,String updateTime);

    @Update("update starfall.user set email=#{email},update_time=#{updateTime} where user=#{user}")
    int updateEmail(String user,String email,String updateTime);

    @Update("update starfall.user_personalized set signature=#{signature},update_time=#{updateTime} where user=#{user}")
    int updateSignature(String user,String signature,String updateTime);

    @Update("update starfall.user_personalized set " +
            "online_name=#{onlineName}, show_online_name=#{showOnlineName}," +
            "show_collection=#{showCollection},show_birthday=#{showBirthday}," +
            "show_gender=#{showGender},show_email=#{showEmail},update_time=#{updateTime} " +
            "where user=#{user}")
    int updatePersonalized(UserPersonalized userPersonalized);

    @Update("update starfall.user_personalized set online_uuid=#{onlineUuid},update_time=#{updateTime} where user=#{user}")
    int updateOnlineUuid(String user,String onlineUuid,String updateTime);

    @Update("update starfall.user_personalized set online_name=#{onlineName},update_time=#{updateTime} where user=#{user}")
    int updateOnlineName(String user,String onlineName,String updateTime);
}
