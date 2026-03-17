package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedalMapper extends Medal{
    String user;
    String gainTime;
    String expireTime;

    @Override
    public String toString() {
        return "MedalMapper{" +
                "user='" + user + '\'' +
                ", gainTime='" + gainTime + '\'' +
                ", expireTime='" + expireTime + '\'' +
                ", id='" + id + '\'' +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", source='" + source + '\'' +
                ", createTime='" + createTime + '\'' +
                "} " + super.toString();
    }
}
