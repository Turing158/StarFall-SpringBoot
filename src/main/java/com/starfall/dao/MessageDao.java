package com.starfall.dao;

import com.starfall.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface MessageDao {
    @Select("select from_user as fromUser,to_user as toUser,date,content,name as fromName,avatar as fromAvatar from starfall.chat_notice c join starfall.user u on c.from_user = u.user where to_user = #{user}")
    List<Message> findAllMsgByToUser(String user);


    @Select("select from_user as fromUser,to_user as toUser,date,content,name as fromName,avatar as fromAvatar from starfall.chat_notice c join starfall.user u on c.from_user = u.user where to_user = #{toUser} and from_user = #{fromUser}")
    List<Message> findMsgByToUserAndFromUser(String toUser,String fromUser);
}
