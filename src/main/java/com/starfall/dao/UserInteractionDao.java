package com.starfall.dao;

import com.starfall.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserInteractionDao {

    //UserNotice
    @Select("select * from starfall.user_notice where (user = #{user}  and status = 0) or type = 'all' order by create_time desc limit 1")
    UserNotice findLastNotice(String user);

    @Select("select count(*) from (select 1 from starfall.user_notice where user = #{user} and status = 0 limit 100) as un")
    int findUnreadNum(String user);

    @Select("select * from starfall.user_notice where id = #{id}")
    UserNotice findUserNoticeById(String id);

    @Select("select * from starfall.user_notice where user=#{user} or type='all' order by create_time desc limit #{index},#{limit}")
    List<UserNotice> findAllUserNotice(String user,int index,int limit);

    @Select("select count(*) from starfall.user_notice where user=#{user} or type='all'")
    int countAllUserNotice(String user);

    @Select("select * from starfall.user_notice where user=#{user} and action like #{actionPart} order by create_time desc limit 1")
    UserNotice findUserNoticeByActionPart(String user, String actionPart);

    @Insert("insert into starfall.user_notice (id, user, create_time, title, type, status, action) values (#{id}, #{user}, #{createTime}, #{title}, #{type}, #{status}, #{action})")
     int insertUserNotice(UserNotice userNotice);

    @Update("update starfall.user_notice set status = #{status} where id = #{id} and type != 'all'")
    int updateUserNoticeStatus(String id, int status);

    @UpdateProvider(type = DaoServiceProvider.class, method = "batchUpdateStatus")
    int UpdateBatchUserNotice(List<UserNotice> userNotices);

    @Update("update starfall.user_notice set status = 1 where user = #{user} and type != 'all'")
    int updateUserNoticeStatus1ByUser(String user);

    @Update("update starfall.user_notice set action = #{action} where id = #{id}")
    int updateUserNoticeAction(String id, String action);

    @Delete("delete from starfall.user_notice where id = #{id}")
    int deleteUserNotice(String id);


    //FriendApplication
    @Select("select * from starfall.friend_application where id = #{id}")
    FriendApplication findFriendApplicationById(String id);

    @Select("select * from  starfall.friend_application where from_user=#{fromUser} and to_user=#{toUser} and status=0")
    FriendApplication findFriendApplicationAndStatus0(String fromUser,String toUser);

    @Select("select count(*) from  starfall.friend_application where from_user=#{fromUser} and to_user=#{toUser} and status=0")
    int countFriendApplicationAndStatus0(String fromUser,String toUser);

    @Insert("insert into starfall.friend_application (id,from_user,to_user,date,reason,status) values (#{id},#{fromUser},#{toUser},#{date},#{reason},#{status})")
    int insertFriendApplication(FriendApplication friendApplication);

    @Update("update starfall.friend_application set status = #{status} where id = #{id}")
    int updateFriendApplicationStatus(String id, int status);




    //FriendRelation
    @Select("select count(*) from starfall.friend_relation where from_user=#{user}")
    int countFriendRelation(String user);

    @Select("select count(*) from starfall.friend_relation where from_user=#{user} and to_user=#{friend}")
    int countFriendRelationWithFriend(String user, String friend);

    @Select("select fr.to_user as user,u.name as name,u.avatar as avatar,fr.alias as alias,fr.is_top as isTop,cn.date,cn.content as lastMsg,fr.relation " +
            "from starfall.friend_relation fr " +
            "left join starfall.user u on fr.to_user = u.user " +
            "left join (" +
                "select *, row_number() over (" +
                    "partition by case when from_user = #{user} then to_user else from_user end order by date desc" +
                ") as rn from starfall.chat_notice where (from_user = #{user} or to_user = #{user})" +
            ") cn on (" +
                "(cn.from_user = #{user} and cn.to_user = fr.to_user)" +
                "or (cn.to_user = #{user} and cn.from_user = fr.to_user)" +
            ") and cn.rn = 1 where fr.from_user = #{user} " +
            "order by fr.is_top desc ,CASE WHEN cn.date IS NULL THEN 1 ELSE 0 END desc,cn.date desc limit #{index},15")
    List<Friend> findAllFriendsWithLastNotice(int index, String user);

    @Select("select * from starfall.friend_relation where from_user=#{user} and to_user=#{friend}")
    FriendRelation findFriendRelation(String user, String friend);

    @Insert("insert into starfall.friend_relation " +
            "(id,from_user,to_user,relation,alias,create_time,update_time,is_top) values " +
            "(#{fromUserRelation.id},#{fromUserRelation.fromUser},#{fromUserRelation.toUser},#{fromUserRelation.relation},#{fromUserRelation.alias},#{fromUserRelation.createTime},#{fromUserRelation.updateTime},#{fromUserRelation.isTop})," +
            "(#{toUserRelation.id},#{toUserRelation.fromUser},#{toUserRelation.toUser},#{toUserRelation.relation},#{toUserRelation.alias},#{toUserRelation.createTime},#{toUserRelation.updateTime},#{toUserRelation.isTop})")
    int insertFriendRelation(@Param("fromUserRelation") FriendRelation fromUserRelation,@Param("toUserRelation") FriendRelation toUserRelation);

    @Update("update starfall.friend_relation set alias = #{alias}, update_time = #{updateTime} where from_user = #{user} and to_user = #{friend}")
    int updateFriendAlias(String user, String friend, String alias, String updateTime);

    @Update("update starfall.friend_relation set is_top = #{isTop}, update_time = #{updateTime} where from_user = #{user} and to_user = #{friend}")
    int updateFriendIsTop(String user, String friend, int isTop, String updateTime);

    @Update("update starfall.friend_relation set relation = #{relation}, update_time = #{updateTime} where from_user = #{user} and to_user = #{friend}")
    int updateFriendRelation(String user, String friend, int relation, String updateTime);

    @Delete("delete from starfall.friend_relation where (from_user = #{user} and to_user = #{friend}) or (from_user = #{friend} and to_user = #{user})")
    int deleteFriendRelation(String user, String friend);

    //ChatNotice
    @Select("select " +
            "from_user as fromUser,fu.name as fromName,fu.avatar as fromAvatar,to_user as toUser,tu.name as toName,tu.avatar as toAvatar,date,content " +
            "from (" +
            "select * from starfall.chat_notice c where (to_user = #{toUser} and from_user = #{fromUser}) or (to_user = #{fromUser} and from_user= #{toUser}) order by date desc limit #{index},20" +
            ") as c " +
            "join starfall.user fu on c.from_user = fu.user join starfall.user tu on c.to_user = tu.user " +
            "order by date")
    List<Message> findMsgByToUserAndFromUser(String toUser, String fromUser,int index);

    @Select("select from_user as fromUser,fu.name as fromName,fu.avatar as fromAvatar,to_user as toUser,tu.name as toName,tu.avatar as toAvatar,date,content from starfall.chat_notice c join starfall.user fu on c.from_user = fu.user join starfall.user tu on c.to_user = tu.user where from_user = #{fromUser} and to_user = #{toUser} order by date desc")
    List<Message> findFromUserMsgByFromUserAndToUser(String fromUser,String toUser);

    @Update("update starfall.chat_notice set content = #{content} where from_user = #{fromUser} and to_user = #{toUser} and date = #{date}")
    int updateMsgContent(String fromUser,String toUser,String date,String content);

    @Insert("insert into starfall.chat_notice(from_user,to_user,date,content) values(#{fromUser},#{toUser},#{date},#{content})")
    int insertMsg(Message message);

    @Delete("delete from starfall.chat_notice where (from_user = #{user} and to_user = #{friend}) or (from_user = #{friend} and to_user = #{user})")
    int deleteMsgByUserAndFriend(String user, String friend);
}
