package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    int topicId;
    String user;
    String date;
    String content;

    int oldTopicId;
    String oldUser;
    String oldDate;
}
