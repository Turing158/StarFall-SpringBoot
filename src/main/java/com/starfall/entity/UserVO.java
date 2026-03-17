package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
// 用户输出实体
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    String user;
    String name;
    int gender;
    String email;
    String birthday;
    int exp;
    int level;
    int maxExp;
    String avatar;
    String role;
    public UserVO(String user, String name, int gender, String email, String birthday, int exp, int level, String avatar, String role){
        this.user = user;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.birthday = birthday;
        this.exp = exp;
        this.level = level;
        this.avatar = avatar;
        this.role = role;
        this.maxExp = Exp.getMaxExp(level);
    }
    public void orderMaxExp(){
        this.maxExp = Exp.getMaxExp(level);
    }

    public User toUser(){
        return new User(user,null,name,gender,email,birthday,exp,level,avatar,role,null,null);
    }
}
