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
}
