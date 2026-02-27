package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// 评论输出实体
public class CommentOut {
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
}
