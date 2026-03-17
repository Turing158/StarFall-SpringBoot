package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
