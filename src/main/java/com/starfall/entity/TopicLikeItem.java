package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicLikeItem {
    int id;
    String title;
    String label;
    String user;
    String name;
    int like;
    int dislike;
}
