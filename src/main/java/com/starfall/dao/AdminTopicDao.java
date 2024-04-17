package com.starfall.dao;

import com.starfall.entity.Topic;
import com.starfall.entity.TopicItem;
import com.starfall.entity.TopicOut;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminTopicDao {
    @Select("select * from starfall.topic join starfall.topicitem t on topic.id = t.topicId order by id desc limit #{page},10")
    List<TopicOut> findAllTopic(int page);
    @Select("select count(*) from starfall.topic")
    int countTopic();
    @Select("select count(*) from starfall.topic where id=#{id}")
    int existTopicById(int id);
    @Insert("insert into starfall.topic value (#{id},#{title},#{label},#{user},#{date},#{view},0,#{version})")
    int addTopic(Topic topic);
    @Insert("insert into starfall.topicitem value (#{id},#{topicTitle},#{enTitle},#{source},#{author},#{language},#{address},#{download},#{content})")
    int addTopicItem(int id,TopicItem topicItem);
    @Update("update starfall.topic set title=#{title},label=#{label},user=#{user},date=#{date},view=#{view},version=#{version} where id=#{id}")
    int updateTopic(Topic topic);
    @Update("update starfall.topicitem set topicTitle=#{topicTitle},enTitle=#{enTitle},source=#{source},author=#{author},language=#{language},address=#{address},download=#{download},content=#{content} where topicId=#{id}")
    int updateTopicItem(int id,TopicItem topicItem);
    @Delete("delete from starfall.topic where id=#{id}")
    int deleteTopic(int id);

}
