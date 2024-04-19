package com.starfall.dao;

import com.starfall.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Transactional
public interface AdminTopicDao {
    @Select("select * from starfall.topic left join starfall.topicitem t on topic.id = t.topicId order by id desc limit #{page},10")
    List<TopicOut> findAllTopic(int page);
    @Select("select count(*) from starfall.topic limit 1")
    int countTopic();
    @Select("select count(*) from starfall.topic where id=#{id} limit 1")
    int existTopicById(int id);
    @Select("select id from starfall.topic order by id desc limit 1")
    int findLastTopicId();
    @Select("select id,title,label,user from starfall.topic")
    List<Topic> findAllTopicSelect();
    @Select("select * from starfall.comment where topicId=#{topicId} order by date desc limit #{page},10")
    List<Comment> findTopicCommentById(int topicId, int page);
    @Select("select count(*) from starfall.comment where topicId=#{topicId} limit 1")
    int countTopicCommentById(int topicId);
    @Select("select count(*) from starfall.comment where topicId=#{topicId} and user=#{user} and date=#{date} limit 1")
    int existComment(int topicId,String user,String date);
    @Insert("insert into starfall.comment value (#{topicId},#{user},#{date},#{content})")
    int addComment(Comment comment);
    @Insert("insert into starfall.topic value (#{id},#{title},#{label},#{user},#{date},#{view},0,#{version})")
    int addTopic(Topic topic);
    @Insert("insert into starfall.topicitem value (#{topicId},#{topicTitle},#{enTitle},#{source},#{author},#{language},#{address},#{download},#{content})")
    int addTopicItem(TopicItem topicItem);
    @Update("update starfall.topic set id=#{topic.id},title=#{topic.title},label=#{topic.label},user=#{topic.user},date=#{topic.date},view=#{topic.view},version=#{topic.version} where id=#{oldId}")
    int updateTopic(int oldId,Topic topic);
    @Update("update starfall.topicitem set topicId=#{topicItem.topicId},topicTitle=#{topicItem.topicTitle},enTitle=#{topicItem.enTitle},source=#{topicItem.source},author=#{topicItem.author},language=#{topicItem.language},address=#{topicItem.address},download=#{topicItem.download},content=#{topicItem.content} where topicId=#{oldId}")
    int updateTopicItem(int oldId,TopicItem topicItem);
    @Update("update starfall.comment set topicId=#{topicId},user=#{user},date=#{date},content=#{content} where topicId=#{oldTopicId} and user=#{oldUser} and date=#{oldDate}")
    int updateComment(Comment comment);
    @Delete("delete from starfall.topic where id=#{id}")
    int deleteTopic(int id);
    @Delete("delete from starfall.topicitem where topicId=#{topicId}")
    int deleteTopicItem(int topicId);
    @Delete("delete from starfall.comment where topicId=#{topicId} and user=#{user} and date=#{date}")
    int deleteComment(Comment comment);

    @Select("select t.id,t.title,t.label,u.user,u.name,(select count(*) from starfall.likelog where topicId=t.id and status=1 limit 1) as 'like',(select count(*) from starfall.likelog where topicId=t.id and status=2 limit 1) as dislike from starfall.topic t join starfall.user u on t.user = u.user order by id desc limit #{page},10")
    List<TopicLikeItem> findAllTopicItem(int page);

    @Select("select topicId, l.user, status, date, name from starfall.likelog l join starfall.user u on l.user = u.user where topicId = #{id} and status != 0 order by date desc limit #{page},10")
    List<LikeItem> findLikeItemByTopicId(int id,int page);
    @Select("select count(*) from starfall.likelog where topicId = #{id} limit 1")
    int countLikeItemByTopicId(int id);
    @Select("select count(*) from starfall.likelog where topicId = #{topicId} and user = #{user} and status = #{status} limit 1")
    int existLikeItem(int topicId,String user,int status);
    @Select("select count(*) from starfall.likelog where topicId = #{topicId} and user = #{user} limit 1")
    int existLikeItemOutStatus(int topicId,String user);
    @Insert("insert into starfall.likelog value (#{topicId},#{user},#{status},#{date})")
    int addLikeItem(LikeLog likeLog);
    @Update("update starfall.likelog set status=#{status},date=#{date} where topicId=#{topicId} and user=#{user}")
    int updateLikeItem(LikeLog likeLog);
    @Delete("delete from starfall.likelog where topicId=#{topicId} and user=#{user} and date=#{date}")
    int deleteLikeItem(LikeLog likeLog);
}
