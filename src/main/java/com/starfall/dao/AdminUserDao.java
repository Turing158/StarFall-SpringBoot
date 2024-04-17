package com.starfall.dao;

import com.starfall.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminUserDao {
    @Select("select * from starfall.user")
    List<User> findAllUser();
}
