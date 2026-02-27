package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRelation {
    String id;
    String fromUser;
    String toUser;
    int relation;
    String alias;
    String createTime;
    String updateTime;
    int isTop;
}
