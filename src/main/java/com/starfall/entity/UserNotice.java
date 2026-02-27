package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotice {
    String id;
    String user;
    String createTime;
    String title;
    UserNoticeType type;
    int status;
    String action;
}
