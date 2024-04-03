package com.starfall.entity;

import com.starfall.util.Exp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicIn {
    int id;
    String title;
    String label;
    String topicTitle;
    String enTitle;
    String source;
    String version;
    String author;
    String language;
    String address;
    String download;
    String content;
    String code;
}
