create table if not exists advertisement
(
    id       varchar(24)  not null
        primary key,
    title    varchar(50)  null,
    file     varchar(100) null,
    date     datetime     null,
    link     varchar(100) null,
    position varchar(10)  null,
    sequence int          null
);

create table if not exists chat_notice
(
    from_user varchar(40)   null,
    to_user   varchar(40)   null,
    date      datetime      null,
    content   varchar(6000) null
);

create table if not exists collection
(
    user    varchar(40) null,
    topicId varchar(24) null,
    date    datetime    null
);

create table if not exists comment
(
    topicId varchar(24)  null,
    user    varchar(40)  null,
    date    datetime     null,
    content varchar(200) null,
    weight  int          null
);

create table if not exists friend_application
(
    id        varchar(26) not null
        primary key,
    from_user varchar(40) null,
    to_user   varchar(40) null,
    date      datetime    null,
    reason    varchar(50) null,
    status    tinyint     null comment '0未处理，1接受，-1拒绝'
);

create table if not exists friend_relation
(
    id          varchar(26) not null
        primary key,
    from_user   varchar(40) null,
    to_user     varchar(40) null,
    relation    tinyint     null comment '0正常，-1拉黑',
    alias       varchar(20) null,
    create_time datetime    null,
    update_time datetime    null,
    is_top      tinyint     null comment '是否顶置'
)
    comment '-1拉黑，0免打扰，1正常';

create table if not exists home_talk
(
    id      varchar(24) not null
        primary key,
    user    varchar(40) null,
    content varchar(80) null,
    date    datetime    null
);

create table if not exists likelog
(
    topicId varchar(24) null,
    user    varchar(40) null,
    status  int         null,
    date    datetime    null
);

create table if not exists live_broadcast
(
    id         varchar(27)  not null comment '格式：lvb年月日时分秒4位毫秒随机6位数'
        primary key,
    user       varchar(20)  null,
    url        varchar(150) null,
    operator   varchar(20)  null,
    reason     varchar(150) null comment '理由',
    platform   varchar(20)  null comment '平台',
    play_uuid  varchar(50)  null,
    apply_time datetime     null,
    status     int          null comment '0：未审核，1：可观看，-1：未通过审核|封禁，'
)
    comment '直播审核表';

create table if not exists medal
(
    id          varchar(23)  null comment '格式：m+yyyyMMddHHmmssSSSS+随机四位字符串',
    icon        varchar(100) null,
    name        varchar(50)  null,
    description varchar(100) null,
    source      varchar(30)  null comment 'h',
    create_time datetime     null
);

create table if not exists medal_mapper
(
    user        varchar(40) null,
    medal       varchar(23) null,
    gain_time   datetime    null,
    expire_time datetime    null
);

create table if not exists notice
(
    id      int          not null
        primary key,
    content varchar(255) null
);

create table if not exists sign_in
(
    user    varchar(40)  null,
    date    date         null,
    message varchar(100) null,
    emotion varchar(10)  null
);

create table if not exists topic
(
    id            varchar(24)  not null
        primary key,
    title         varchar(100) null,
    label         varchar(10)  null,
    user          varchar(40)  not null,
    date          date         null,
    view          int          null,
    comment       int          null,
    version       varchar(100) null,
    refresh       datetime     null,
    display       tinyint(1)   null comment '-1禁止显示，1显示，0草稿',
    belong        varchar(20)  null comment '资源、文章',
    isFirstPublic tinyint(1)   null comment '0未发布过，1已发布过'
);

create table if not exists topic_file
(
    id          varchar(26) not null
        primary key,
    user        varchar(40) null,
    topicId     varchar(24) null,
    upload_date datetime    null,
    fileName    varchar(25) null,
    fileLabel   varchar(50) null,
    fileSize    mediumtext  null
);

create table if not exists topic_gallery
(
    id          varchar(26)  not null
        primary key,
    topicId     varchar(24)  null,
    user        varchar(40)  null,
    path        varchar(255) null,
    label       varchar(255) null,
    upload_date datetime     null
);

create table if not exists topicitem
(
    topicId    varchar(24)   null,
    topicTitle varchar(100)  null,
    enTitle    varchar(100)  null,
    source     varchar(10)   null,
    author     varchar(50)   null,
    language   varchar(100)  null,
    address    varchar(255)  null,
    download   varchar(255)  null,
    content    varchar(8126) null
);

create table if not exists user
(
    user        varchar(40)  not null
        primary key,
    password    varchar(100) null,
    name        varchar(20)  null,
    gender      int          null,
    email       varchar(255) null,
    birthday    date         null,
    exp         int          null,
    level       int          null,
    avatar      varchar(255) null,
    role        varchar(20)  null,
    create_time datetime     null,
    update_time datetime     null
);

create table if not exists user_notice
(
    id          varchar(26)                                    not null
        primary key,
    user        varchar(40)                                    null,
    create_time datetime                                       null,
    title       varchar(255)                                   null,
    type        enum ('topic', 'live', 'friend', 'msg', 'all') null comment 'topic关于主题消息，live关于直播消息，msg关于官方消息，friend关于好友消息，all关于给所有人发布的活动消息',
    status      tinyint                                        null comment '0未观看，1已观看',
    action      json                                           null comment '需要跳转的连接'
);

create table if not exists user_personalized
(
    user             varchar(40)  null,
    signature        varchar(500) null,
    online_uuid      varchar(50)  null,
    online_name      varchar(50)  null,
    show_online_name tinyint(1)   null comment '1展示，0隐藏',
    show_collection  tinyint(1)   null comment '1展示，0隐藏',
    show_birthday    tinyint(1)   null comment '1展示，0隐藏',
    show_gender      tinyint(1)   null,
    show_email       tinyint(1)   null,
    create_time      datetime     null,
    update_time      datetime     null
);

