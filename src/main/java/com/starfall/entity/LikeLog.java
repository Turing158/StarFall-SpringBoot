package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeLog implements Serializable {
    int topicId;
    String user;
    int status;
    String date;
}
