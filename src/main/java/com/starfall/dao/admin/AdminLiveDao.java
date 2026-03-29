package com.starfall.dao.admin;

import com.starfall.entity.LiveBroadcast;
import com.starfall.entity.LiveBroadcastHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminLiveDao {

    @Select("select lb.*,u.name as name,o.name as operatorName from starfall.live_broadcast lb left join starfall.user u on lb.user = u.user left join starfall.user o on lb.operator = o.user limit #{page}, 10")
    List<LiveBroadcastHistory> findAllLive(int page);

    @Select("select count(*) from starfall.live_broadcast")
    int countAllLive();

    @Select("select * from starfall.live_broadcast where id = #{id}")
    LiveBroadcast findLiveById(String id);

    @Insert("insert into starfall.live_broadcast(id,user,url,reason,operator,platform,play_uuid,apply_time,status) values(#{id},#{user},#{url},#{reason},#{operator},#{platform},#{playUuid},#{applyTime},#{status})")
    int insertLive(LiveBroadcast live);

    @Update("update starfall.live_broadcast set user=#{user},url=#{url},reason=#{reason},operator=#{operator},platform=#{platform},play_uuid=#{playUuid},apply_time=#{applyTime},status=#{status} where id = #{id}")
    int updateLive(LiveBroadcast live);

    @Delete("delete from starfall.live_broadcast where id = #{id}")
    int deleteLive(String id);
}
