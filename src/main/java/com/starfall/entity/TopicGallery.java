package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicGallery {
    String id;
    String topicId;
    String user;
    String uploadDate;
    String path;
    String label;
}
