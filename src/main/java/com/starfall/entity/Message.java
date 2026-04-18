package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    String fromUser;
    String fromName;
    String fromAvatar;
    String toUser;
    String toName;
    String toAvatar;
    String date;
    String content;
    boolean canNotice;
    public Message(String fromUser, String toUser, String date, String content) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.date = date;
        this.content = content;
    }
}


