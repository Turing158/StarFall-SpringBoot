package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Medal {
    String id;
    String icon;
    String name;
    String description;
    String source;
    String createTime;
}
