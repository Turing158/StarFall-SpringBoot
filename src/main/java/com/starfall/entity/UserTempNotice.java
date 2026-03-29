package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserTempNotice {
    String title;
    String content;
    String createTime;
    UserNoticeType type = UserNoticeType.tmp;

    public UserTempNotice(String title, String content, String createTime) {
        this.title = title;
        this.content = content;
        this.createTime = createTime;
    }
}
