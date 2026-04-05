package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// 主题输出实体
public class TopicOut {
    String id;
    String title;
    String label;
    String date;
    String refresh;
    int display;
    String belong;
    int isFirstPublic;
    int view;
    int comment;
    //主题详细内容
    String topicTitle;
    String enTitle;
    String source;
    String version;
    String author;
    String language;
    String address;
    String download;
    String content;
    //这里是主题用户的一些信息
    String user;
    String name;
    int exp;
    int level;
    String avatar;
    int maxExp;
    String signature;

    public Topic parseTopic() {
        return new Topic(id, title, label, user, name, avatar, date, view, comment, version, refresh, display, belong, isFirstPublic);
    }

    public TopicOut(Topic topic,TopicItem topicItem,User user,UserPersonalized userPersonalized){
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.label = topic.getLabel();
        this.date = topic.getDate();
        this.refresh = topic.getRefresh();
        this.display = topic.getDisplay();
        this.belong = topic.getBelong();
        this.isFirstPublic = topic.getIsFirstPublic();
        this.view = topic.getView();
        this.comment = topic.getComment();

        this.topicTitle = topicItem.getTopicTitle();
        this.enTitle = topicItem.getEnTitle();
        this.source = topicItem.getSource();
        this.version = topic.getVersion();
        this.author = topicItem.getAuthor();
        this.language = topicItem.getLanguage();
        this.address = topicItem.getAddress();
        this.download = topicItem.getDownload();
        this.content = topicItem.getContent();

        this.user = topic.getUser();
        this.name = topic.getName();
        this.exp = user.getExp();
        this.level = user.getLevel();
        this.avatar = user.getAvatar();
        this.maxExp = Exp.getMaxExp(user.getLevel());
        this.signature = userPersonalized.getSignature();
    }
}
