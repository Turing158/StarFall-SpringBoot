package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// 评论输出实体
public class CommentVO {
    String topicId;
    String date;
    String content;
    int weight;
    String user;
    String name;
    String avatar;
    int level;
    int exp;
    final int maxExp = Exp.getMaxExp(level);
    String signature;

    public CommentVO(String topicId,String user,String date,String content,int weight){
        this.topicId = topicId;
        this.user = user;
        this.date = date;
        this.content = content;
        this.weight = weight;
    }
}
