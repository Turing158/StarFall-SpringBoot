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
    String date;
    String content;
}
