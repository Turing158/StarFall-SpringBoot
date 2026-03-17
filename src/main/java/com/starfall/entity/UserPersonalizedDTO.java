package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserPersonalizedDTO extends UserPersonalized{
    String code;

    public UserPersonalized toUserPersonalized(){
        return new UserPersonalized(user,signature,onlineName,showOnlineName,showCollection,showBirthday,showGender,showEmail,createTime,updateTime);
    }
}
