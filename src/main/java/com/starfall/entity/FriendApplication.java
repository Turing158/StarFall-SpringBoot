package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendApplication {
    String id;
    String fromUser;
    String toUser;
    String date;
    String reason;
    int status;
}
