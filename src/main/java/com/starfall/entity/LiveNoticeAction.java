package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiveNoticeAction {
    String id;
    String url;
    String reason;
    String operator;
    boolean status;
}
