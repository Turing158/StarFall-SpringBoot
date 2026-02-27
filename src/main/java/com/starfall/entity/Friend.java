package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Friend {
    String user;
    String name;
    String avatar;
    String alias;
    int isTop;
    String date;
    String lastMsg;
    int relation;
}
