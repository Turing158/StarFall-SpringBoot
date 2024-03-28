package com.starfall.dao;

import com.starfall.entity.CommentOut;
import com.starfall.entity.Topic;
import com.starfall.entity.TopicOut;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface TopicDao {

    @Select("select * from starfall.topic t join starfall.user u on t.user = u.user order by date desc limit #{num},10")
    List<Topic> findAllTopic(int num);

    @Select("select * from starfall.topic t join starfall.user u on t.user = u.user where label = #{label} order by date desc limit #{num},10")
    List<Topic> findAllTopicLabel(int num,String label);

    @Select("select * from starfall.topic t join starfall.user u on t.user = u.user where version = #{version} order by date desc limit #{num},10")
    List<Topic> findAllTopicVersion(int num,String version);

    @Select("select * from starfall.topic t join starfall.user u on t.user = u.user where label = #{label} and version = #{version} order by date desc limit #{num},10")
    List<Topic> findAllTopicLabelAndVersion(int num,String label,String version);

    @Select("select count(*) from starfall.topic")
    int findTopicTotal();
    @Select("select count(*) from starfall.topic where label = #{label}")
    int findTopicTotalByLabel();
    @Select("select count(*) from starfall.topic where version = #{version}")
    int findTopicTotalByVersion();
    @Select("select count(*) from starfall.topic where label = #{label} and version = #{version}")
    int findTopicTotalByLabelAndVersion();

    @Select("select * from starfall.topic t join starfall.topicitem ti on t.id = ti.topicId join starfall.user u on u.user = t.user where t.id = #{id}")
    TopicOut findTopicInfoById(int id);


    @Select("select * from starfall.topic t join starfall.user u on t.user = u.user where u.user = #{user} limit #{num},10")
    List<Topic> findTopicByUser(int num,String user);


    @Select("select count(*) from starfall.topic where user = #{user}")
    int findTopicTotalByUser(String user );

    @Select("select distinct version from starfall.topic order by version desc")
    List<String> findTopicVersion();


    @Select("select * from starfall.comment c join starfall.user u on c.user = u.user where topicid = #{id} order by date desc limit #{num},10")
    List<CommentOut> findCommentByTopicId(int id, int num);


    @Select("select count(*) from starfall.comment where topicid = #{id}")
    int findCommentCountByTopicId(int id);

    @Insert("insert into starfall.comment (topicid,user,date,content) values (#{topicid},#{user},#{date},#{content})")
    int insertComment(int topicid,String user,String date,String content);
}
