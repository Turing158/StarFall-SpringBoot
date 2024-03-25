package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topic implements Serializable {
    int id;
    String title;
    String label;
    String user;
    String name;
    String avatar;
    String date;
    int view;
    int comment;
    String version;
}
