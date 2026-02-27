package com.starfall.dao;

import com.starfall.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Transactional
public interface AdminTopicDao {
    @Select("select * from starfall.topic left join starfall.topicitem t on topic.id = t.topicId where title like #{keyword} or user like #{keyword} or id like #{keyword} order by id desc limit #{page},10")
    List<TopicOut> findAllTopic(int page,String keyword);
    @Select("select * from starfall.topic where id=#{id}")
    TopicOut findTopicById(String id);
    @Select("select count(*) from starfall.topic where title like #{keyword} or user like #{keyword} or id like #{keyword} limit 1")
    int countTopic(String keyword);
    @Select("select count(*) from starfall.topic where id=#{id} limit 1")
    int existTopicById(String id);
    @Select("select id from starfall.topic order by id desc limit 1")
    int findLastTopicId();
    @Select("select id from starfall.topic where user=#{user}")
    List<String> findAllTopicId(String user);
    @Select("select id,title,label,user from starfall.topic where title like #{keyword} or user like #{keyword} or id like #{keyword} limit #{num}")
    List<Topic> findAllTopicSelect(String keyword,int num);
    @Select("select * from starfall.comment where topicId=#{topicId} and (user like #{keyword} or content like #{keyword}) order by weight desc,date desc limit #{page},10")
    List<Comment> findTopicCommentById(String topicId, int page,String keyword);
    @Select("select count(*) from starfall.comment where topicId=#{topicId} and (user like #{keyword} or content like #{keyword}) limit 1")
    int countTopicCommentById(String topicId,String keyword);
    @Select("select count(*) from starfall.comment where topicId=#{topicId} and user=#{user} and date=#{date} limit 1")
    int existComment(String topicId,String user,String date);
    @Select("select t.id as topicId,t.title as title,t.label as label,tu.user as authorUser,tu.name as authorName, c.user as user, c.date as date from starfall.collection c left join starfall.topic t on c.topicId = t.id left join starfall.user u on c.user = u.user left join starfall.user tu on t.user = tu.user where c.user = #{user} order by c.date desc limit #{num},20")
    List<TopicCollection> findCollectByUser(String user,int num);
    @Select("select count(*) from starfall.collection where topicId = #{topicId} and user = #{user} limit 1")
    int existCollection(String topicId,String user);
    @Select("select count(*) from starfall.collection where user = #{user} limit 1")
    int countCollectByUser(String user);
    @Select("select * from starfall.topic_gallery where topicId=#{topicId}")
    List<TopicGallery> findAllTopicGallery(String topicId);
    @Select("select count(*) from starfall.topic_gallery where topicId=#{topicId}")
    int countTopicGalleryByTopicId(String topicId);
    @Select("select * from starfall.topic_gallery where id=#{id} limit 1")
    TopicGallery findTopicGalleryById(String id);
    @Select("select * from starfall.topic_file where topicId=#{topicId}")
    List<TopicFile> findAllTopicFile(String topicId);
    @Select("select count(*) from starfall.topic_file where topicId=#{topicId}")
    int countTopicFileByTopicId(String topicId);
    @Select("select * from starfall.topic_file where id=#{id} limit 1")
    TopicFile findTopicFileById(String id);
    @Insert("insert into starfall.topic_gallery value (#{id},#{topicId},#{user},#{path},#{label},#{uploadDate})")
    int insertTopicGallery(TopicGallery topicGallery);
    @Insert("insert into starfall.topic_file value (#{id},#{user},#{topicId},#{uploadDate},#{fileName},#{fileLabel},#{fileSize})")
    int insertTopicFile(TopicFile topicFile);
    @Insert("insert into starfall.comment value (#{topicId},#{user},#{date},#{content},#{weight})")
    int addComment(Comment comment);
    @Insert("insert into starfall.topic value (#{id},#{title},#{label},#{user},#{date},#{view},0,#{version},#{refresh},#{display},#{belong},#{isFirstPublic})")
    int addTopic(Topic topic);
    @Insert("insert into starfall.topicitem value (#{topicId},#{topicTitle},#{enTitle},#{source},#{author},#{language},#{address},#{download},#{content})")
    int addTopicItem(TopicItem topicItem);
    @Insert("insert into starfall.collection value (#{user},#{topicId},#{date})")
    int addCollection(String topicId,String user,String date);
    @Update("update starfall.topic set title=#{title},label=#{label},user=#{user},date=#{date},view=#{view},version=#{version},refresh=#{refresh},display=#{display},belong=#{belong},isFirstPublic=#{isFirstPublic} where id=#{id}")
    int updateTopic(Topic topic);
    @Update("update starfall.topicitem set topicTitle=#{topicTitle},enTitle=#{enTitle},source=#{source},author=#{author},language=#{language},address=#{address},download=#{download},content=#{content} where topicId=#{topicId}")
    int updateTopicItem(TopicItem topicItem);
    @Update("update starfall.comment set topicId=#{topicId},user=#{user},date=#{date},content=#{content} where topicId=#{oldTopicId} and user=#{oldUser} and date=#{oldDate}")
    int updateComment(Comment comment);
    @Delete("delete from starfall.topic where id=#{id}")
    int deleteTopic(String id);
    @Delete("delete from starfall.topicitem where topicId=#{topicId}")
    int deleteTopicItem(String topicId);
    @Delete("delete from starfall.comment where topicId=#{topicId} and user=#{user} and date=#{date}")
    int deleteComment(Comment comment);
    @Select("select t.id,t.title,t.label,u.user,u.name,(select count(*) from starfall.likelog where topicId=t.id and status=1 limit 1) as 'like',(select count(*) from starfall.likelog where topicId=t.id and status=2 limit 1) as dislike from starfall.topic t join starfall.user u on t.user = u.user where t.title like #{keyword} or t.id like #{keyword} or t.user like #{keyword} order by id desc limit #{page},10")
    List<TopicLikeItem> findAllTopicLikeItem(int page,String keyword);
    @Select("select topicId, l.user, status, date, name from starfall.likelog l join starfall.user u on l.user = u.user where topicId = #{id} and status != 0 and (l.user like #{keyword}) order by date desc limit #{page},10")
    List<LikeItem> findLikeItemByTopicId(String id,int page,String keyword);
    @Select("select count(*) from starfall.likelog where topicId = #{id} and (user like #{keyword}) limit 1")
    int countLikeItemByTopicId(String id,String keyword);
    @Select("select count(*) from starfall.likelog where topicId = #{topicId} and user = #{user} and status = #{status} limit 1")
    int existLikeItem(String topicId,String user,int status);
    @Select("select count(*) from starfall.likelog where topicId = #{topicId} and user = #{user} limit 1")
    int existLikeItemOutStatus(String topicId,String user);
    @Insert("insert into starfall.likelog value (#{topicId},#{user},#{status},#{date})")
    int addLikeItem(LikeLog likeLog);
    @Update("update starfall.likelog set status=#{status},date=#{date} where topicId=#{topicId} and user=#{user}")
    int updateLikeItem(LikeLog likeLog);

    @Delete("delete from starfall.topic where user=#{user}")
    int deleteTopicByUser(String user);

    @Delete("delete from starfall.topicitem where topicId=#{topicId}")
    int deleteTopicItemByTopicId(String topicId);

    @Delete("delete from starfall.comment where user=#{user}")
    int deleteCommentByUser(String user);

    @Delete("delete from starfall.likelog where user=#{user}")
    int deleteLikeLogByUser(String user);

    @Delete("delete from starfall.collection where user=#{user} and topicId=#{topicId}")
    int deleteCollection(String user,String topicId);

    @Delete("delete from starfall.topic_gallery where id=#{id}")
    int deleteTopicGalleryById(String id);
    @Delete("delete from starfall.topic_file where id=#{id}")
    int deleteTopicFileById(String id);

    @Delete("delete from starfall.collection where user=#{user}")
    int deleteCollectionOnlyUser(String user);

    @Delete("delete from starfall.topic_gallery where topicId=#{topicId}")
    int deleteTopicGalleryByTopicId(String topicId);

    @Delete("delete from starfall.topic_gallery where user=#{user}")
    int deleteTopicGalleryByUser(String user);

    @Delete("delete from starfall.topic_file where topicId=#{topicId}")
    int deleteTopicFileByTopicId(String topicId);

    @Delete("delete from starfall.topic_file where user=#{user}")
    int deleteTopicFileByUser(String user);
}
