package com.starfall.entity.admin;

import com.starfall.entity.FriendRelation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRelationAdminVO extends FriendRelation {
    String fromUserName;
    String toUserName;

    @Override
    public String toString() {
        return "FriendRelationAdminVO{" +
                "fromUserName='" + fromUserName + '\'' +
                ", toUserName='" + toUserName + '\'' +
                "} " + super.toString();
    }
}
