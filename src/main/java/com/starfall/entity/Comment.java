package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// 用于更改评论时传递旧值的实体
public class Comment {
    String topicId;
    String user;
    String date;
    String content;
    int weight;

    String oldTopicId;
    String oldUser;
    String oldDate;
}
