package com.starfall.entity.admin;


import com.starfall.entity.UserNotice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNoticeAdminVO extends UserNotice {
    String name;
    boolean sendNotice;
}
