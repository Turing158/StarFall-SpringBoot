package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicDTO {
    String id;
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
    int display;
    String belong;
    String code;
}
