package com.starfall.dao;

import com.starfall.entity.SignIn;
import com.starfall.entity.User;
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

    @Insert("insert into starfall.user values(#{user},#{password},#{name},#{gender},#{email},#{birthday},#{exp},#{level},#{avatar},#{role})")
    int insertUser(User user);

    @Insert("insert into starfall.sign_in values(#{user},#{date},#{message},#{emotion})")
    int insertSignIn(SignIn signIn);

    @Update("update starfall.user set name=#{name},gender=#{gender},email=#{email},birthday=#{birthday},exp=#{exp},level=#{level},role=#{role} where user=#{user}")
    int updateUser(User user);

    @Update("update starfall.user set password=#{password} where user=#{user}")
    int updatePassword(User user);

    @Update("update starfall.user set avatar=#{avatar} where user=#{user}")
    int updateAvatar(String user,String avatar);

    @Update("update starfall.sign_in set message=#{message},emotion=#{emotion} where user=#{user} and date=#{date}")
    int updateSignIn(SignIn signIn);

    @Delete("delete from starfall.user where user=#{user}")
    int deleteUser(String user);

    @Delete("delete from starfall.sign_in where user=#{user} and date=#{date}")
    int deleteSignIn(SignIn signIn);

     @Delete("delete from starfall.sign_in where user=#{user}")
    int deleteSignInByUser(String user);

}
