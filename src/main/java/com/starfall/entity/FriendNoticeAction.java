package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendNoticeAction {
    String id;
    String name;
    String user;
    String avatar;
    String reason;
    int status;
    boolean handled;
}
