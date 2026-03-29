package com.starfall.dao.admin;

import com.starfall.entity.FriendApplication;
import com.starfall.entity.FriendRelation;
import com.starfall.entity.UserNotice;
import com.starfall.entity.admin.FriendRelationAdminVO;
import com.starfall.entity.admin.UserNoticeAdminVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminUserInteractionDao {
    //user_notice
    @Select("select * from starfall.user_notice un left join starfall.user u on un.user = u.user where un.user=#{user} and un.type=#{type} order by un.create_time desc limit #{num},10")
    List<UserNoticeAdminVO> findAllUserNoticeByType(String user,int num, String type);

    @Select("select * from starfall.user_notice un left join starfall.user u on un.user = u.user where un.user=#{user} or un.type='all' order by un.create_time desc limit #{num},10")
    List<UserNoticeAdminVO> findAllUserNotice(String user,int num);

    @Select("select * from starfall.user_notice where type='all' order by create_time desc limit #{num},10")
    List<UserNoticeAdminVO> findAllUserNoticeByTypeAll(int num);

    @Select("select count(*) from starfall.user_notice where user=#{user} and type=#{type}")
    int countAllUserNoticeByType(String user,String type);

    @Select("select count(*) from starfall.user_notice where user=#{user} or type='all'")
    int countAllUserNotice(String user);

    @Select("select count(*) from starfall.user_notice where type='all'")
    int countAllUserNoticeByTypeAll();

    @Insert("insert into starfall.user_notice(id,user,create_time,title,type,status,action) " +
            "values(#{id},#{user},#{createTime},#{title},#{type},#{status},#{action})")
    void insertUserNotice(UserNotice userNotice);

    @Update("update starfall.user_notice set id=#{id},user=#{user},create_time=#{createTime},title=#{title},type=#{type},status=#{status},action=#{action} where id=#{id}")
    void updateUserNotice(UserNotice userNotice);

    @Update("update starfall.user_notice set status=#{status} where id=#{id}")
    void updateUserNoticeRead(String id, int status);

    @Delete("delete from starfall.user_notice where id=#{id}")
    void deleteUserNotice(String id);


    //friend_application
    @Select("select * from starfall.friend_application where from_user=#{user} limit #{page},10")
    List<FriendApplication> findAllFriendApplication(String user,int page);

    @Select("select count(*) from starfall.friend_application where from_user=#{user}")
    int countAllFriendApplication(String user);

    @Select("select count(*) from starfall.friend_application where from_user=#{fromUser} and to_user=#{toUser} and status=0")
    int countFriendApplicationByFromUserAndToUserAndStatus0(String fromUser, String toUser);

    @Select("select * from starfall.friend_application where id=#{id}")
    FriendApplication findFriendApplicationById(String id);

     @Insert("insert into starfall.friend_application(id,from_user,to_user,date,reason,status) " +
             "values(#{id},#{fromUser},#{toUser},#{date},#{reason},#{status})")
     void insertFriendApplication(FriendApplication friendApplication);

     @Update("update starfall.friend_application set from_user=#{fromUser},to_user=#{toUser},date=#{date},reason=#{reason},status=#{status} where id=#{id}")
     void updateFriendApplication(FriendApplication friendApplication);

     @Delete("delete from starfall.friend_application where id=#{id}")
     void deleteFriendApplication(String id);

     //friend_relation
    @Select("select *,fu.name as fromUserName,tu.name as toUserName from starfall.friend_relation fr " +
            "left join starfall.user fu on fu.user=fr.from_user left join starfall.user tu on tu.user=fr.to_user " +
            "limit #{page},10")
    List<FriendRelationAdminVO> findAllFriendRelation(int page);

    @Select("select count(*) from starfall.friend_relation")
    int countAllFriendRelation();

    @Select("select count(*) from starfall.friend_relation where (from_user=#{fromUser} and to_user=#{toUser}) or (from_user=#{toUser} and to_user=#{fromUser})")
    int countFriendRelation(String fromUser, String toUser);

    @Select("select fr.id from starfall.friend_relation as fr,(select * from starfall.friend_relation where id=#{id}) as frid where fr.from_user = frid.to_user and fr.to_user = frid.from_user")
    String findOtherFriendRelationById(String id);

    @Insert("insert into starfall.friend_relation(id,from_user,to_user,relation,alias,create_time,update_time,is_top) values" +
            "(#{fromUserRelation.id},#{fromUserRelation.fromUser},#{fromUserRelation.toUser},#{fromUserRelation.relation},#{fromUserRelation.alias},#{fromUserRelation.createTime},#{fromUserRelation.updateTime},#{fromUserRelation.isTop})," +
            "(#{toUserRelation.id},#{toUserRelation.fromUser},#{toUserRelation.toUser},#{toUserRelation.relation},#{toUserRelation.alias},#{toUserRelation.createTime},#{toUserRelation.updateTime},#{toUserRelation.isTop})")
    void insertFriendRelation(@Param("fromUserRelation") FriendRelation fromUserRelation,@Param("toUserRelation") FriendRelation toUserRelation);

    @Update("update starfall.friend_relation set relation=#{relation},alias=#{alias},create_time=#{createTime},update_time=#{updateTime},is_top=#{isTop} where id=#{id}")
    void updateFriendRelation(FriendRelation friendRelation);

    @Delete("delete from starfall.friend_relation where id=#{id}")
    void deleteFriendRelation(String id);
}
