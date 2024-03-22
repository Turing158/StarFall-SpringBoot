package com.starfall.dao;

import com.starfall.entity.User;
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

    @Insert("insert into starfall.user (user, password,name,gender,birthday,exp,level) " +
            "values (#{user},#{password},#{name},#{gender},#{birthday},#{exp},#{level})")
    int insertUser(User user);



    @Update("update into starfall.user set exp=#{exp} where user=#{user}")
    int updateExp(String user,int exp);

    @Update("update into starfall.user set name=#{name},gender=#{gender},birthday=#{birthday} where user=#{user}")
    int updateInfo(String user,String name,String gender,String birthday);


    @Update("update into starfall.user set password=#{password} where user=#{user}")
    int updatePassword(String user,String password);
}
