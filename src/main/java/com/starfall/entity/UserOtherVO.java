package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOtherVO extends UserVO {
    String signature;
    String onlineName;
    int showOnlineName;
    int showCollection;
    int showBirthday;
    int showGender;
    int showEmail;


    public UserOtherVO(User userObj, UserPersonalized up) {
        super(
                userObj.getUser(),
                userObj.getName(),
                up.getShowGender() == 1 ? userObj.getGender() : -1,
                up.getShowEmail() == 1 ? userObj.getEmail() : null,
                up.getShowBirthday() == 1 ? userObj.getBirthday() : null,
                userObj.getExp(), userObj.getLevel(),
                Exp.getMaxExp(userObj.getLevel()),userObj.getAvatar(),
                userObj.getRole()
        );
        this.signature = up.getSignature();
        this.onlineName = up.getShowOnlineName() == 1 ? up.getOnlineName() : null;
        this.showOnlineName = up.getShowOnlineName();
        this.showCollection = up.getShowCollection();
        this.showBirthday = up.getShowBirthday();
        this.showGender = up.getShowGender();
        this.showEmail = up.getShowEmail();
    }

}
