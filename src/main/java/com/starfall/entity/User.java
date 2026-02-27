package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    String user;
    String password;
    String name;
    int gender;
    String email;
    String birthday;
    int exp;
    int level;
    String avatar;
    String role;
    String createTime;
    String updateTime;

    public UserDTO toUserDTO(){
        return new UserDTO(user,name,gender,email,birthday,exp,level,avatar,role);
    }
}
