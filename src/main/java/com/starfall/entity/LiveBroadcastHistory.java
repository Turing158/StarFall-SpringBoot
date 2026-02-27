package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiveBroadcastHistory {
    String id;
    String user;
    String name;
    String url;
    String reason;
    String operator;
    String operatorName;
    String platform;
    String playUuid;
    String applyTime;
    int status;
}
