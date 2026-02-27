package com.starfall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "topic_search")
public class Search {
    @Id
    String id;//keyword
    @Field(type = FieldType.Text)
    String title;//text
    @Field(type = FieldType.Keyword)
    String label;//keyword
    @Field(type = FieldType.Keyword)
    String belong;//keyword
    @Field(type = FieldType.Text)
    String topicTitle;//text
    @Field(type = FieldType.Text)
    String enTitle;//text
    @Field(type = FieldType.Text)
    String content;//text
    @Field(type = FieldType.Date)
    LocalDate date;//date
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    LocalDateTime refreshTime;//date_time
    @Field(type = FieldType.Keyword)
    String user;//keyword
    @Field(type = FieldType.Text)
    String name;//text
    @Field(type = FieldType.Integer)
    int status;//integer

    public Search(String user, String name) {
        this.user = user;
        this.name = name;
    }
}
