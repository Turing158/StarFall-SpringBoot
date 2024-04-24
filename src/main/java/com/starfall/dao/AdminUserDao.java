package com.starfall.dao;

import com.starfall.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminUserDao {
    @Select("select * from starfall.user")
    List<User> findAllUser();

    @Select("select * from starfall.user limit #{page},10")
    List<User> findUserByPage(int page);
    @Select("select count(*) from starfall.user limit 1")
    int countUser();

    @Select("select count(*) from starfall.user where user=#{user} limit 1")
    int existUser(String user);

    @Select("select count(*) from starfall.user where email=#{email} limit 1")
    int existEmail(String email);

    @Select("select * from starfall.user where user=#{user} limit 1")
    User findUserByUser(String user);

    @Insert("insert into starfall.user values(#{user},#{password},#{name},#{gender},#{email},#{birthday},#{exp},#{level},#{avatar},#{role})")
    int insertUser(User user);

    @Update("update starfall.user set user=#{user.user},password=#{user.password},name=#{user.name},gender=#{user.gender},email=#{user.email},birthday=#{user.birthday},exp=#{user.exp},level=#{user.level},avatar=#{user.avatar},role=#{user.role} where user=#{oldUser}")
    int updateUser(User user,String oldUser);

    @Update("update starfall.user set avatar=#{avatar} where user=#{user}")
    int updateAvatar(String user,String avatar);

    @Delete("delete from starfall.user where user=#{user}")
    int deleteUser(String user);

}
