package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOtherVO extends UserVO {
    String user;
    String signature;
    String onlineName;
    int showOnlineName;
    int showCollection;
    int showBirthday;
    int showGender;
    int showEmail;
}
