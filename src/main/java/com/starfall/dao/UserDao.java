package com.starfall.dao;

import com.starfall.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
