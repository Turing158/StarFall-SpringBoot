package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicCollection extends Collection{
    String title;
    String label;
    String authorName;
    String authorUser;
}
