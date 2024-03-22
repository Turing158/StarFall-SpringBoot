package com.starfall.dao;

import com.starfall.entity.Topic;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TopicDao {

    @Select("select * from starfall.topic")
    List<Topic> findAllTopic();

}
