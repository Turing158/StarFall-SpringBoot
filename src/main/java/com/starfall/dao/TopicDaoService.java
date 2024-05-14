package com.starfall.dao;

import org.apache.ibatis.jdbc.SQL;

public class TopicDaoService {
    public String search(String key,String classification,int page){
        return new SQL(){
            {
                SELECT("t.id," +
                        "t.title," +
                        "t.label," +
                        "regexp_replace(regexp_replace(regexp_replace(regexp_replace(regexp_replace(regexp_replace(content,'<.+?>',''),'\\\\*\\\\*(.*?)\\\\*\\\\*', '$1'),'\\\\[(.*?)\\\\]\\\\((.*?)\\\\)', '$1'),'\\>\\\\s', ''),'#', ''),'\\\\*{3}|\\\\*\\\\s\\\\*\\\\s\\\\*', '') as content," +
                        "t.view," +
                        "t.comment," +
                        "t.date," +
                        "t.user," +
                        "u.name");
                FROM("starfall.topic t");
                JOIN("starfall.topicitem ti on t.id = ti.topicId");
                JOIN("starfall.user u on t.user = u.user");
                if(classification.equals("作者")){
                    WHERE("u.name like #{key}");
                }
                else if(classification.equals("主题")){
                    WHERE("t.title like #{key}");
                }
                else if(classification.equals("内容")){
                    WHERE("ti.content like #{key}");
                }
                else{
                    WHERE("t.title like #{key} or u.name like #{key} or ti.content like #{key}");
                }
                ORDER_BY("t.date desc");
                LIMIT("#{page},10");
            }
        }.toString();
    }

    public String searchTotal(String key,String classification){
        return new SQL(){
            {
                SELECT("count(*)");
                FROM("starfall.topic t");
                JOIN("starfall.topicitem ti on t.id = ti.topicId");
                JOIN("starfall.user u on t.user = u.user");
                if(classification.equals("作者")){
                    WHERE("u.name like #{key}");
                }
                else if(classification.equals("主题")){
                    WHERE("t.title like #{key}");
                }
                else if(classification.equals("内容")){
                    WHERE("ti.content like #{key}");
                }
                else{
                    WHERE("t.title like #{key} or u.name like #{key} or ti.content like #{key}");
                }
            }
        }.toString();
    }

}
