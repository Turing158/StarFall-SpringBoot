package com.starfall.dao;

import com.starfall.entity.Topic;
import com.starfall.entity.TopicOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface TopicDao {

    @Select("select * from starfall.topic limit #{num},10")
    List<Topic> findAllTopic(int num);

    @Select("select * from starfall.topic t join starfall.topicitem ti on t.id = ti.topicId join starfall.user u on u.user = t.user where t.id = #{id}")
    TopicOut findTopicInfoById(int id);
}
