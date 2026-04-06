package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeTalk {
    String id;
    String user;
    String name;
    String avatar;
    String content;
    String date;

    public HomeTalk(String id, String user, String content, String date) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.date = date;
    }
}
