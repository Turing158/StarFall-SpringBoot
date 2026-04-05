package com.starfall.dao;

import com.starfall.entity.SignIn;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SignInDao {
    @Select("select * from starfall.sign_in where user = #{user} order by date desc limit #{page},#{limit}")
    List<SignIn> findAllSignInByUser(String user,int page,int limit);

    @Select("select count(*) from starfall.sign_in where user = #{user}")
    int countSignInByUser(String user);

    @Select("with user_signins as (" +
            "    select user,date,LAG(date) over (partition by user order by date) as prev_date" +
            "    from starfall.sign_in where user = #{user}" +
            "),continuous_check as (" +
            " select user, date," +
            "   case" +
            "       when prev_date is null or datediff(date, prev_date) > 1" +
            "           then 1 else 0" +
            "       end as is_new_start " +
            "from user_signins" +
            "),sign_group as (" +
            "    select user, date, SUM(is_new_start) over (partition by user order by date) as group_id" +
            "    from continuous_check)" +
            "select IFNULL((" +
            "    select count(*) from sign_group" +
            "    where group_id = (" +
            "    select distinct group_id from sign_group" +
            "    where date = curdate() - interval 1 day or date = curdate()" +
            "    )" +
            "), 0) as count")
    int countContinualSignIn(String user);

    @Select("select count(*) from starfall.sign_in where user = #{user} and date = curdate()")
    int countTodaySignIn(String user);

    @Insert("insert into starfall.sign_in value (#{user},#{date},#{message},#{emotion})")
    int insertSignIn(SignIn signIn);
}
