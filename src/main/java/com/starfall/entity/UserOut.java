package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOut {
    String user;
    String name;
    int gender;
    String email;
    String birthday;
    int exp;
    int level;
}
