package com.starfall.dao;

import com.starfall.entity.SignIn;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SignInDao {

    @Select("select * from starfall.sign_in where user = #{user} and date = #{date} order by date desc")
    SignIn findSignInByUser(String user,String date);


    @Select("select * from starfall.sign_in where user = #{user} order by date desc limit #{page},6")
    List<SignIn> findAllSignInByUser(String user,int page);

    @Select("select count(*) from starfall.sign_in where user = #{user}")
    int countSignInByUser(String user);

    @Insert("insert into starfall.sign_in value (#{user},#{date},#{message},#{emotion})")
    int insertSignIn(String user,String date,String message,String emotion);
}
