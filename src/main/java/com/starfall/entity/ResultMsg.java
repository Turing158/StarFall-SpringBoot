package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultMsg {
    String msg;
    Object object;
    int num;
    public ResultMsg(String msg,Object object) {
        this.msg = msg;
        this.object = object;
    }
}
