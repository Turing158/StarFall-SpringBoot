package com.starfall.dao.admin;

import com.starfall.entity.*;
import com.starfall.entity.admin.MedalMapperAdminDTO;
import com.starfall.entity.admin.UserPersonalizedAdminDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminUserDao {
    @Select("select * from starfall.user limit #{num},10")
    List<User> findAllUser(int num);

    @Select("select * from starfall.user where user like #{keyword} or name like #{keyword} limit #{num}")
    List<User> findAllUserByUserOrName(String keyword,int num);

    @Select("select count(*) from starfall.user")
    int countAllUser();

    @Select("select count(*) from starfall.user where user like #{keyword} or name like #{keyword}")
    int countAllUserByUserOrName(String keyword);

    @Select("select * from starfall.user where user like #{keyword} or name like #{keyword} or email like #{keyword} limit #{page},10")
    List<User> findUserByPage(int page,String keyword);

    @Select("select count(*) from starfall.user where user like #{keyword} or name like #{keyword} or email like #{keyword} limit 1")
    int countUser(String keyword);

    @Select("select count(*) from starfall.user where user=#{user} limit 1")
    int existUser(String user);

    @Select("select count(*) from starfall.user where email=#{email} limit 1")
    int existEmail(String email);

    @Select("select * from starfall.user where user=#{user} limit 1")
    User findUserByUser(String user);

    @Select("select * from starfall.sign_in join starfall.user u on sign_in.user = u.user where u.user like #{keyword} or u.name like #{keyword} or sign_in.date like #{keyword} order by date desc limit #{page},10")
    List<SignIn> findSignInByPage(int page,String keyword);

    @Select("select count(*) from starfall.sign_in join starfall.user u on sign_in.user = u.user where u.user like #{keyword} or u.name like #{keyword} or sign_in.date like #{keyword}")
    int countSignIn(String keyword);

    @Select("select count(*) from starfall.sign_in where user=#{user} and date=#{date} limit 1")
    int existSignIn(String user,String date);

    @Insert("insert into starfall.user values(#{user},#{password},#{name},#{gender},#{email},#{birthday},#{exp},#{level},#{avatar},#{role},#{createTime},#{updateTime})")
    int insertUser(User user);

    @Insert("insert into starfall.sign_in values(#{user},#{date},#{message},#{emotion})")
    int insertSignIn(SignIn signIn);

    @Update("update starfall.user set name=#{name},gender=#{gender},email=#{email},birthday=#{birthday},exp=#{exp},level=#{level},role=#{role} where user=#{user}")
    int updateUser(User user);

    @Update("update starfall.user set create_time=#{createTime} where user=#{user}")
    int updateCreateTime(String user,String createTime);

    @Update("update starfall.user set password=#{password} where user=#{user}")
    int updatePassword(User user);

    @Update("update starfall.user set avatar=#{avatar},update_time=#{updateTime} where user=#{user}")
    int updateAvatar(String user,String avatar,String updateTime);

    @Update("update starfall.sign_in set message=#{message},emotion=#{emotion} where user=#{user} and date=#{date}")
    int updateSignIn(SignIn signIn);

    @Delete("delete from starfall.user where user=#{user}")
    int deleteUser(String user);

    @Delete("delete from starfall.sign_in where user=#{user} and date=#{date}")
    int deleteSignIn(SignIn signIn);

     @Delete("delete from starfall.sign_in where user=#{user}")
     int deleteSignInByUser(String user);

     //personalized
    @Select("select * from starfall.user_personalized up left join starfall.user u on up.user = u.user limit #{num},10")
    List<UserPersonalizedAdminDTO> findAllPersonalized(int num);

    @Select("select count(*) from starfall.user_personalized")
    int countAllPersonalized();

    @Insert("insert into starfall.user_personalized values(#{user},#{signature},null,#{onlineName},#{showOnlineName},#{showCollection},#{showBirthday},#{showGender},#{showEmail},#{createTime},#{updateTime})")
    int insertPersonalized(UserPersonalized userPersonalized);

    @Update("update starfall.user_personalized set " +
            "signature=#{signature}," +
            "online_name=#{onlineName}," +
            "show_online_name=#{showOnlineName}," +
            "show_collection=#{showCollection}," +
            "show_birthday=#{showBirthday}," +
            "show_gender=#{showGender}," +
            "show_email=#{showEmail}," +
            "update_time=#{updateTime}" +
            " where user=#{user}")
    int updatePersonalized(UserPersonalized userPersonalized);

    @Delete("delete from starfall.user_personalized where user=#{user}")
    int deletePersonalized(String user);

     //medalMapper
     @Select("select *,u.name as userName from starfall.medal_mapper mm left join starfall.medal m on mm.medal = m.id left join starfall.user u on mm.user = u.user limit #{num},10")
     List<MedalMapperAdminDTO> findMedalMapperByUser(int num);

    @Select("select count(*) from starfall.medal_mapper")
    int countMedalMapper();

    @Select("select count(*) from starfall.medal_mapper where user=#{user} and medal=#{medal} limit 1")
    int countMedalMapperByUserAndId(String user,String medal);

    @Insert("insert into starfall.medal_mapper values(#{user},#{id},#{gainTime},#{expireTime})")
    int insertMedalMapper(MedalMapper medalMapper);

    @Update("update starfall.medal_mapper set gain_time=#{gainTime},expire_time=#{expireTime} where user=#{user} and medal=#{id}")
    int updateMedalMapper(MedalMapper medalMapper);

    @Delete("delete from starfall.medal_mapper where user=#{user} and medal=#{medal}")
    int deleteMedalMapper(String user,String medal);

    //medal
    @Select("select * from starfall.medal order by create_time desc limit #{num},10")
    List<Medal> findMedal(int num);

    @Select("select count(*) from starfall.medal")
    int countMedal();

    @Insert("insert into starfall.medal values(#{id},#{icon},#{name},#{description},#{source},#{createTime})")
    int insertMedal(Medal medal);

    @Update("update starfall.medal set name=#{name},description=#{description},icon=#{icon} where id=#{id}")
    int updateMedal(Medal medal);

    @Delete("delete from starfall.medal where id=#{id}")
    int deleteMedal(String id);
}
