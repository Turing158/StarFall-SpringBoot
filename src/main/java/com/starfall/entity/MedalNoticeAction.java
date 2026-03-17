package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MedalNoticeAction {
    String id;
    String medal;
    String icon;
    String description;
    String gainTime;
    String expireTime;
}
