package com.starfall.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicNoticeAction {
    String id;
    String title;
    int status;
    String operator;
    String reason;
}
