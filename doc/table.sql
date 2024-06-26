create table notice
(
    id      int          not null
        primary key,
    content varchar(255) null
);




create table user
(
    user     varchar(40)  not null
        primary key,
    password varchar(100) null,
    name     varchar(20)  null,
    gender   int          null,
    email    varchar(255) null,
    birthday date         null,
    exp      int          null,
    level    int          null,
    avatar   varchar(255) null,
    role     varchar(20) null
);


create table chat_notice
(
    from_user varchar(40)  null,
    to_user   varchar(40)  null,
    date      datetime     null,
    content   varchar(255) null
);



create table sign_in
(
    user    varchar(40)  null,
    date    date         null,
    message varchar(100) null,
    emotion varchar(10)  null
);

create table topic
(
    id      int          not null
        primary key,
    title   varchar(100) null,
    label   varchar(10)  null,
    user    varchar(40)  not null,
    date    date         null,
    view    int          null,
    comment int          null,
    version varchar(100) null,
    constraint topic_user_user_fk
        foreign key (user) references user (user)
);

create table likelog
(
    topicId int         null,
    user    varchar(40) null,
    status  int         null,
    date    datetime    null,
    constraint likeLog_topic_id_fk
        foreign key (topicId) references topic (id)
);

create table topicitem
(
    topicId    int           null,
    topicTitle varchar(100)  null,
    enTitle    varchar(100)  null,
    source     varchar(10)   null,
    author     varchar(50)   null,
    language   varchar(100)  null,
    address    varchar(255)  null,
    download   varchar(255)  null,
    content    varchar(8126) null,
    constraint topicItem_topic_id_fk
        foreign key (topicId) references topic (id)
);

create table comment
(
    topicId int           null,
    user    varchar(40)   null,
    date    datetime      null,
    content varchar(8164) null
);