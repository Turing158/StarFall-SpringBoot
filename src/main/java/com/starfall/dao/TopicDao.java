package com.starfall.dao;

import com.starfall.entity.Topic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface TopicDao {

    @Select("select * from starfall.topic limit #{num},10")
    List<Topic> findAllTopic(int num);

}
