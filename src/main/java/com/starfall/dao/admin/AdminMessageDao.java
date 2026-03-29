package com.starfall.dao.admin;

import com.starfall.entity.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminMessageDao {
    @Select("select fu.user as fromUser,fu.name as fromName,tu.user as toUser,tu.name as toName,date,content from starfall.chat_notice join starfall.user fu on fu.user=chat_notice.from_user join starfall.user tu on tu.user=chat_notice.to_user where fu.user like #{keyword} or fu.name like #{keyword} or tu.user like #{keyword} or tu.name like #{keyword} or content like #{keyword} order by date desc limit #{page},10")
    List<Message> findAllMessage(int page,String keyword);

    @Select("select count(*) from starfall.chat_notice join starfall.user fu on fu.user=chat_notice.from_user join starfall.user tu on tu.user=chat_notice.to_user where fu.user like #{keyword} or fu.name like #{keyword} or tu.user like #{keyword} or tu.name like #{keyword} or content like #{keyword}")
    int countMessage(String keyword);

    @Select("select count(*) from starfall.chat_notice where from_user=#{fromUser} and to_user=#{toUser} and date=#{date}")
    int existMessage(String fromUser,String toUser,String date);

    @Insert("insert into starfall.chat_notice values(#{fromUser},#{toUser},#{date},#{content})")
    int insertMessage(Message message);

    @Update("update starfall.chat_notice set content=#{content} where from_user=#{fromUser} and to_user=#{toUser} and date=#{date}")
    int updateMessage(Message message);

    @Update("update starfall.chat_notice set from_user=#{newMessage.fromUser},to_user=#{newMessage.toUser},date=#{newMessage.date},content=#{newMessage.content} where from_user=#{oldMessage.fromUser} and to_user=#{oldMessage.toUser} and date=#{oldMessage.date}")
    int updateMessageByOldMessage(Message newMessage,Message oldMessage);
    @Delete("delete from starfall.chat_notice where from_user=#{fromUser} and to_user=#{toUser} and date=#{date}")
    int deleteMessage(String fromUser,String toUser,String date);

    @Delete("delete from starfall.chat_notice where from_user=#{user} or to_user=#{user}")
    int deleteMessageByUser(String user);

}
