package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendDeleteNoticeAction {
    String user;
    String name;
    String alias;
    boolean isDelete;
    boolean deleteAllMsg;
}
