package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// 主题输出实体
public class TopicOut {

    String id;
    String title;
    String label;
    String date;
    int display;
    String belong;
    int isFirstPublic;
    int view;
    int comment;
    String topicTitle;
    String enTitle;
    String source;
    String version;
    String author;
    String language;
    String address;
    String download;
    String content;
    String user;
    String name;
    int exp;
    int level;
    String avatar;
    int maxExp;
}
