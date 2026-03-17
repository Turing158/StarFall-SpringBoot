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
    String refresh;
    int display;
    String belong;
    int isFirstPublic;
    int view;
    int comment;
    //主题详细内容
    String topicTitle;
    String enTitle;
    String source;
    String version;
    String author;
    String language;
    String address;
    String download;
    String content;
    //这里是主题用户的一些信息
    String user;
    String name;
    int exp;
    int level;
    String avatar;
    int maxExp;
    String signature;
}
