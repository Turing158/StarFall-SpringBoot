package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisStatusDTO {
    String ping;
    boolean connected;

//    服务器信息
    String redisVersion;
    String os;
    String uptimeInDays;
//    内存信息
    String usedMemoryHuman;
    int totalKeys;
    // 统计信息
    String totalConnectionsReceived;
    String totalCommandsProcessed;
    String instantaneousOpsPerSec;

    // 客户端信息
    String connectedClients;
    String blockedClients;

    String error;
}
