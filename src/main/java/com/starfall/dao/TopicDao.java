package com.starfall.dao;

import com.starfall.entity.Topic;
import com.starfall.entity.TopicOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface TopicDao {

    @Select("select * from starfall.topic t join starfall.user u on t.user = u.user limit #{num},10")
    List<Topic> findAllTopic(int num);

    @Select("select count(*) from starfall.topic")
    int findTopicTotal();

    @Select("select * from starfall.topic t join starfall.topicitem ti on t.id = ti.topicId join starfall.user u on u.user = t.user where t.id = #{id}")
    TopicOut findTopicInfoById(int id);


    @Select("select * from starfall.topic t join starfall.user u on t.user = u.user where u.user = #{user} limit #{num},10")
    List<Topic> findTopicByUser(int num,String user);


    @Select("select count(*) from starfall.topic where user = #{user}")
    int findTopicTotalByUser(String user );
}
