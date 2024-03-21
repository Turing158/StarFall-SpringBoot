package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicItem implements Serializable {
    int topicId;
    String topicTitle;
    String enTitle;
    String source;
    String version;
    String author;
    String language;
    String address;
    String download;
}
