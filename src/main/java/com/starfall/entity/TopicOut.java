package com.starfall.entity;

import com.starfall.util.Exp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicOut {

    int id;
    String title;
    String label;
    String date;
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
    int maxExp = Exp.getMaxExp(level);
    String avatar;
    int oldId;
}
