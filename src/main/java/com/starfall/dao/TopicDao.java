package com.starfall.dao;

import com.starfall.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Mapper
@Transactional
public interface TopicDao {

//    主题
    @Select("select * from starfall.topic order by id desc")
    List<Topic> findAll();

    @Select("select * from starfall.topic t join starfall.user u on t.user = u.user where display = 1 and belong = #{belong} order by refresh desc")
    List<Topic> findAllTopic(String belong);

    @Select("select * from starfall.topic t join starfall.user u on t.user = u.user where label = #{label} and belong = #{belong} and display = 1 order by refresh desc")
    List<Topic> findAllTopicLabel(String label,String belong);

    @Select("select * from starfall.topic t join starfall.user u on t.user = u.user where display = 1 and belong = #{belong} order by refresh desc limit #{num},30")
    List<Topic> findAllTopicLimit30(int num,String belong);

    @Select("select * from starfall.topic t join starfall.user u on t.user = u.user where label = #{label} and display = 1 and belong = #{belong} order by refresh desc limit #{num},30")
    List<Topic> findAllTopicLabelLimit30(int num,String label,String belong);

    @Select("select count(*) from starfall.topic where display = 1 and belong = #{belong}")
    int findTopicTotal(String belong);

    @Select("select count(*) from starfall.topic where label = #{label} and belong = #{belong} and display = 1")
    int findTopicTotalByLabel(String label,String belong);

    // 这个查询不到用户名称和头像以及主题的详细内容
    @Select("select * from starfall.topic where id = #{id}")
    Topic findTopicById(String id);

    @Select("select * from starfall.topic t join starfall.topicitem ti on t.id = ti.topicId left join starfall.user u on u.user = t.user left join starfall.user_personalized up on t.user = up.user where t.id = #{id}")
    TopicOut findTopicInfoById(String id);

    @Select("select * from starfall.topic t join starfall.user u on t.user = u.user where u.user = #{user} limit #{num},20")
    List<Topic> findTopicByUser(int num,String user);

    @Select("select * from starfall.topic t join starfall.user u on t.user = u.user where u.user = #{user} and display = 1 limit #{num},20")
    List<Topic> findTopicByUserWhereDisplay(int num,String user);

    @Select("select count(*) from starfall.topic where user = #{user}")
    int findTopicTotalByUser(String user);

    @Select("select count(*) from starfall.topic where DATE(date) = CURDATE() and user=#{user}")
    int countTopicTotalByDateAndUser(String user);

    @Select("select count(*) from starfall.topic where user = #{user} and display = 1")
    int findTopicTotalByUserWhereDisplay(String user);

    @Select("select * from starfall.topic t join starfall.user u on t.user = u.user where display = 1 order by date desc limit 7")
    List<Topic> findFirstPublicTopic();

    @Select("select * from starfall.topic t join starfall.user u on t.user = u.user where display = 1 order by refresh desc limit 7")
    List<Topic> findFirstRefreshTopic();

    @Select("select user from starfall.topic where id = #{id}")
    String findTopicUserById(String id);

    // 已弃用，搜索功能交给Elasticsearch处理
//    @SelectProvider(type = TopicDaoService.class,method = "search")
//    List<Search> searchByKey(String key,String classification,int page);
//    @SelectProvider(type = TopicDaoService.class,method = "searchTotal")
//    int searchTotalByKey(String key,String classification);

    @Insert("insert into starfall.topic values (#{id},#{title},#{label},#{user},#{date},0,0,#{version},#{refresh},#{display},#{belong},#{isFirstPublic})")
    int insertTopic(String id,String title,String label,String user,String date,String version,String refresh,int display,String belong,int isFirstPublic);

    @Insert("insert into starfall.topicitem values (#{topicId},#{subtitle},#{subtitleEn},#{source},#{author},#{language},#{address},#{download},#{content})")
    int insertTopicItem(String topicId,String subtitle,String subtitleEn,String source,String author,String language,String address,String download,String content);

    @Update("update starfall.topic set view = #{view} where id = #{id}")
    int updateTopicView(int view,String id);

    @Update("update starfall.topic set display = #{display} where id = #{id}")
    int updateTopicDisplay(int display,String id);

    @Update("update starfall.topicitem set topicTitle = #{topicTitle},enTitle = #{enTitle},source = #{source},author = #{author},language = #{language},address = #{address},download = #{download},content = #{content} where topicId = #{topicId}")
    int updateTopicItem(TopicItem topicItem);

    @Delete("delete from starfall.topic where id = #{topicId}")
    int deleteTopic(String id);

    @Delete("delete from starfall.topicitem where topicId = #{topicId}")
    int deleteTopicItem(String topicId);

//    评论
    @Select("select * from starfall.comment c join starfall.user u on c.user = u.user join starfall.user_personalized up on c.user = up.user where topicid = #{id} order by date limit #{num},10")
    List<CommentVO> findCommentByTopicId(String id, int num);

    @Select("select * from starfall.comment where topicId=#{topicId} and user=#{user} and date=#{date}")
    Comment findCommentByUserAndTopicIdAndDate(String user,String topicId,String date);

    @Select("select * from starfall.topic t join starfall.user u on t.user = u.user join (select topicId,MAX(date) as last_comment_date from starfall.comment group by topicId) c on t.id = c.topicId where display = 1 order by c.last_comment_date desc limit 7")
    List<Topic> findFirstCommentTopic();

    @Select("select count(*) from starfall.comment where topicId = #{id}")
    int findCommentCountByTopicId(String id);

    @Select("select count(*) from starfall.comment where topicId = #{id} and weight != 0")
    int findCommentTopCountByTopicId(String id);

    @Insert("insert into starfall.comment (topicId,user,date,content,weight) values (#{topicId},#{user},#{date},#{content},#{weight})")
    int insertComment(String topicId,String user,String date,String content,int weight);

    @Update("update starfall.topic set comment = #{comment},refresh=#{refresh} where id = #{id}")
    int updateTopicComment(int comment,String refresh,String id);

    @Update("update starfall.topic set title = #{title},label = #{label},date = #{date},version = #{version},belong = #{belong},isFirstPublic = #{isFirstPublic},refresh = #{refresh} where id = #{id}")
    int updateTopicExpectCommentAndView(Topic topic);

    @Update("update starfall.comment set weight = #{weight} where topicid = #{topicid} and user = #{user} and date = #{date}")
    int updateCommentWeight(String topicid,String user,String date,int weight);

    @Delete("delete from starfall.comment where topicid = #{topicid} and user = #{user} and date = #{date}")
    int deleteComment(String topicid,String user,String date);

    @Delete("delete from starfall.comment where topicId = #{topicId}")
    int deleteCommentByTopicId(String topicId);

//    点赞
    @Select("select count(*) from starfall.likelog where likelog.topicId = #{id} and status = 1")
    int findLikeTotalByTopic(String id);

    @Select("select * from starfall.likelog where topicId = #{id} and user = #{user}")
    LikeLog findLikeByTopicAndUser(String id,String user);

    @Insert("insert into starfall.likelog value (#{id},#{user},#{state},#{date})")
    int insertLike(String id,String user,int state,String date);

    @Update("update starfall.likelog set status = #{status},date = #{date} where topicId = #{id} and user = #{user}")
    int updateLikeStateByTopicAndUser(String id,String user,int status,String date);

    @Delete("delete from starfall.likelog where topicId = #{topicId}")
    int deleteLikeLog(String topicId);

//    收藏
    @Select("select t.id,t.title,t.label,tu.user,tu.name,tu.avatar,t.date,t.view,t.comment,t.version,t.display,t.belong from starfall.collection c left join starfall.topic t on c.topicId = t.id left join starfall.user tu on tu.user = t.user left join starfall.user u on c.user = u.user where c.user = #{user} and t.display = 1 order by c.date desc limit #{num},20")
    List<Topic> findCollectByUser(String user,int num);

    @Select("select count(*) from starfall.collection c left join starfall.topic t on c.topicId = t.id where c.user = #{user} and t.display = 1")
    int countCollectByUser(String user);

    @Select("select count(*) from starfall.collection where topicId = #{id}")
    int findCollectionTotalById(String id);

    @Select("select * from starfall.collection where user = #{user} and topicId = #{id}")
    Collection findCollection(String user,String id);

    @Select("select count(*) from starfall.collection where user = #{user} and topicId = #{id}")
    int existCollection(String user,String id);

    @Insert("insert into starfall.collection (user,topicId,date) values (#{user},#{id},#{date})")
    int appendCollect(String user,String id,String date);

    @Delete("delete from starfall.collection where user = #{user} and topicId = #{id}")
    int deleteCollect(String user,String id);


//    主题画廊
    @Select("select * from starfall.topic_gallery where id = #{id}")
    TopicGallery findTopicGalleryById(String id);

    @Select("select * from starfall.topic_gallery where topicId = #{topicId}")
    List<TopicGallery> findTopicGalleryByTopicId(String topicId);

    @Select("select count(*) from starfall.topic_gallery where topicId = #{topicId}")
    int countTopicGalleryByTopicId(String topicId);

    @Insert("insert into starfall.topic_gallery values (#{id},#{topicId},#{user},#{path},#{label},#{uploadDate})")
    int insertTopicGallery(TopicGallery topicGallery);

    @Delete("delete from starfall.topic_gallery where id = #{id}")
    int deleteTopicGalleryById(String id);

//    主题文件（附件）
    @Select("select * from starfall.topic_file where id = #{id}")
    TopicFile findTopicFileById(String id);

    @Select("select * from starfall.topic_file where topicId = #{topicId}")
    List<TopicFile> findTopicFilesByTopicId(String topicId);

    @Select("select count(*) from starfall.topic_file where topicId = #{topicId}")
    int countTopicFileByTopicId(String topicId);

    @Insert("insert into starfall.topic_file values (#{id},#{user},#{topicId},#{uploadDate},#{fileName},#{fileLabel},#{fileSize},#{path})")
    int insertTopicFile(TopicFile topicFile);

    @Delete("delete from starfall.topic_file where id = #{id}")
    int deleteTopicFileById(String id);
}
