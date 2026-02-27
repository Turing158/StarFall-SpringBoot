package com.starfall.dao;

import com.starfall.entity.LiveBroadcast;
import com.starfall.entity.LiveBroadcastHistory;
import com.starfall.entity.LiveBroadcastShow;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LiveDao {

    @Select("select * from starfall.live_broadcast where status=1 order by apply_time desc limit ${index},10")
    List<LiveBroadcastShow> findAllLiveShow(int index);

    @Select("select lb.*,o.name as operatorName from starfall.live_broadcast lb left join starfall.user o on lb.operator = o.user where lb.user=#{user} order by apply_time desc limit ${page},10")
    List<LiveBroadcastHistory> findAllLiveByUser(String user, int page);

    @Select("select count(*) from starfall.live_broadcast where user=#{user}")
    int countLiveByUser(String user);

    @Select("select count(*) from starfall.live_broadcast where url=#{url} and (status=1 or status=0)")
    int existUrl(String url);

    @Select("select count(*) from starfall.live_broadcast where user=#{user} and DATE(apply_time)=CURDATE() and (status=0 or status=-1)")
    int currentDayLiveCount(String user);

    @Select("select * from starfall.live_broadcast where id=#{id}")
    LiveBroadcast findLiveBroadcast(String id);

    @Select("select * from starfall.live_broadcast where status=0 order by apply_time desc limit ${page},10")
    List<LiveBroadcast> findAllLiveApplyByStatus0(int page);

    @Select("select count(*) from starfall.live_broadcast where status=0")
    int countLiveApplyByStatus0();

    @Insert("insert into starfall.live_broadcast values(#{id},#{user},#{url},#{reason},#{operator},#{platform},#{playUuid},#{applyTime},#{status})")
    int insertLiveBroadcast(LiveBroadcast liveBroadcast);

    @Update("update starfall.live_broadcast set play_uuid=#{playUid},status=#{status},reason=#{reason},operator=#{user} where id=#{id}")
    int updateLiveStatus(String id,String user,String playUid, int status,String reason);

    @Delete("delete from starfall.live_broadcast where id=#{id}")
    int deleteLiveBroadcast(String id);
}
