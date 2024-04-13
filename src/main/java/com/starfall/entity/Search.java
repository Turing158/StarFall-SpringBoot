package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Search {
    String id;
    String title;
    String label;
    String content;
    int view;
    int comment;
    String date;
    String user;
    String name;




}
