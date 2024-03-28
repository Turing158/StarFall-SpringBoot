package com.starfall.entity;

import com.starfall.util.Exp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentOut {
    int topicId;
    String date;
    String content;
    String user;
    String name;
    String avatar;
    int level;
    int exp;
    final int maxExp = Exp.getMaxExp(level);
}
