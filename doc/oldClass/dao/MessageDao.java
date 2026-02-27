package com.starfall.dao;

import com.starfall.entity.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface MessageDao {
    @Select("select from_user as fromUser,fu.name as fromName,fu.avatar as fromAvatar,to_user as toUser,tu.name as toName,tu.avatar as toAvatar,date,content from starfall.chat_notice c join starfall.user fu on c.from_user = fu.user join starfall.user tu on c.to_user = tu.user where to_user = #{user} or from_user = #{user} order by date desc")
    List<Message> findAllMsgByToUser(String user);


    @Select("select from_user as fromUser,fu.name as fromName,fu.avatar as fromAvatar,to_user as toUser,tu.name as toName,tu.avatar as toAvatar,date,content from starfall.chat_notice c join starfall.user fu on c.from_user = fu.user join starfall.user tu on c.to_user = tu.user where (to_user = #{toUser} and from_user = #{fromUser}) or (to_user = #{fromUser} and from_user= #{toUser}) order by date")
    List<Message> findMsgByToUserAndFromUser(String toUser,String fromUser);


    @Select("select from_user as fromUser,fu.name as fromName,fu.avatar as fromAvatar,to_user as toUser,tu.name as toName,tu.avatar as toAvatar,date,content from starfall.chat_notice c join starfall.user fu on c.from_user = fu.user join starfall.user tu on c.to_user = tu.user where from_user = #{fromUser} and to_user = #{toUser} order by date desc")
    List<Message> findFromUserMsgByFromUserAndToUser(String fromUser,String toUser);

    @Update("update starfall.chat_notice set content = #{content} where from_user = #{fromUser} and to_user = #{toUser} and date = #{date}")
    int updateMsgContent(String fromUser,String toUser,String date,String content);

    @Insert("insert into starfall.chat_notice(from_user,to_user,date,content) values(#{fromUser},#{toUser},#{date},#{content})")
    int insertMsg(String fromUser,String toUser,String date,String content);
}
