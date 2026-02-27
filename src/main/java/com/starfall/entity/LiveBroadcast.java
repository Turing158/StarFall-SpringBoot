package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiveBroadcast {
    String id;
    String user;
    String url;
    String reason;
    String operator;
    String platform;
    String playUuid;
    String applyTime;
    int status;
}
