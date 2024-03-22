# user
INSERT INTO starfall.user (user, password, name, gender, email, birthday, exp, level, head) VALUES ('admin', 'b9827fc7ca8d1d8e9901aeede62a3c69', '管理员', 0, 'admin@sf.com', '2024-03-01', 10, 999, null);
INSERT INTO starfall.user (user, password, name, gender, email, birthday, exp, level, head) VALUES ('test', '2679a5a3e44284f50cc484d196d52ee4', '测试账号', 0, 'test@test.com', '2024-03-21', 10, 1, null);


# likelog
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (2, 'admin', 1, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (3, 'admin', 2, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (5, 'admin', 2, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (9, 'admin', 2, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (11, 'admin', 1, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (1, 'asd', 1, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (2, 'asd', 1, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (3, 'asd', 1, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (4, 'asd', 2, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (8, 'asd', 1, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (11, 'asd', 2, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (9, 'asd', 1, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (1, 'test', 1, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (2, 'test', 2, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (3, 'test', 1, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (5, 'test', 2, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (8, 'test', 2, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (10, 'test', 1, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (11, 'test', 1, '2023-08-29');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (1, 'admin', 1, '2023-11-12');
INSERT INTO starfall.likelog (topicId, user, state, date) VALUES (4, 'admin', 1, '2023-09-19');


# notice
INSERT INTO starfall.notice (id, content) VALUES (1, '测试中！后台不会透露任何隐私');
INSERT INTO starfall.notice (id, content) VALUES (2, '欢迎来到星辰倾城StarFall主页');
INSERT INTO starfall.notice (id, content) VALUES (3, '喜欢像素类游戏，不妨试试我的世界');
INSERT INTO starfall.notice (id, content) VALUES (4, '本项目仅用于本人练手，不要乱搞哦！');




# topic
INSERT INTO starfall.topic (id, title, label, user, date, view, comment) VALUES (1, '[1.8.x-1.9.x][Spigot]StarFall空岛生存>巨大更新[物品扩展|粘液科技]', '服务端', 'admin', '2023-11-12', 100, 12);
INSERT INTO starfall.topic (id, title, label, user, date, view, comment) VALUES (2, '[1.12.2-1.8][低配福利]骐的整合---纯净基础整合[持续更新]', '客户端', 'admin', '2023-07-04', 100, 8);
INSERT INTO starfall.topic (id, title, label, user, date, view, comment) VALUES (3, '[1.8.x][Spigot]星辰倾城-起床战争服务端', '服务端', 'admin', '2023-07-02', 100, 3);
INSERT INTO starfall.topic (id, title, label, user, date, view, comment) VALUES (4, '[冰骐解说]我的世界|亡灵战争', '视频', 'admin', '2023-09-19', 100, 6);
INSERT INTO starfall.topic (id, title, label, user, date, view, comment) VALUES (5, '[冰骐]Minecraft双人默契大挑战——两位初三的帅[dou]气[bi]解密系列', '视频', 'admin', '2023-06-14', 100, 6);
INSERT INTO starfall.topic (id, title, label, user, date, view, comment) VALUES (7, '[信息]BeautyIndicator——轻量级的显血插件[1.8-1.12] [接权搬运]', '插件', 'admin', '2023-09-16', 100, 3);
INSERT INTO starfall.topic (id, title, label, user, date, view, comment) VALUES (8, '[娱乐|信息][StarMC]Powder——通过粒子来显示图像！[1.12][接权搬运]', '插件', 'admin', '2023-09-19', 100, 1);
INSERT INTO starfall.topic (id, title, label, user, date, view, comment) VALUES (9, 'SkillAPI教程and案例——来自定义职业吧！', '文章', 'admin', '2023-09-19', 100, 8);
INSERT INTO starfall.topic (id, title, label, user, date, view, comment) VALUES (10, '[娱乐|机制]Advanced Electricity——高科技电力[接权搬运][1.10-1.12]', '插件', 'admin', '2023-07-03', 100, 9);
INSERT INTO starfall.topic (id, title, label, user, date, view, comment) VALUES (11, '[管理|信息][StarMC]MaintenanceMode-维护模式[接权搬运][1.8-1.12]', '插件', 'admin', '2023-09-19', 100, 4);
INSERT INTO starfall.topic (id, title, label, user, date, view, comment) VALUES (12, '[信息]Language Barrier Breaker-多种语言[接权搬运][1.8-1.12] ', '插件', 'admin', '2023-07-05', 100, 2);



# topicitem
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, version, author, language, address, download) VALUES (1, 'StarFall空岛生存', 'StarFall-Skyblock', '原创', '1.8.x-1.9.', 'TuringICE', '简体中文', 'https://www.mcbbs.net/thread-792740-1-1.html', 'http://某.盘.com');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, version, author, language, address, download) VALUES (2, '骐的整合', 'Integration of Qi', '原创', '1.12.2-1.8', '作者', '简体中文', 'https://www.mcbbs.net/thread-1126142-1-1.html', 'https://www.mcbbs.net/thread-1126142-1-1.html');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, version, author, language, address, download) VALUES (3, '星辰倾城-起床战争服务端', 'StarFall-BedWard', '原创', '1.8.x', '作者', '简体中文', 'https://www.mcbbs.net/thread-773917-1-1.html', 'http://www.本贴.com');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, version, author, language, address, download) VALUES (4, '我的世界|亡灵战争', 'Minecraft|War of the Undead', '原创', '1.6', 'TuringICE', '简体中文', 'https://www.mcbbs.net/thread-878770-1-1.html', 'https://www.mcbbs.net/thread-878770-1-1.html');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, version, author, language, address, download) VALUES (5, '双人默契大挑战', 'The Great Challenge of Mutual Understanding', '原创', '无', '作者', '简体中文', 'https://www.mcbbs.net/thread-812503-1-1.html', 'https://www.mcbbs.net/thread-812503-1-1.html');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, version, author, language, address, download) VALUES (7, '轻量级的显血', 'BeautyIndicator', '搬运', '1.8-1.12', 'haelexuis', '简体中文|English', 'https://www.spigotmc.org/resources/beautyindicator-entity-health-in-combat.57546/', 'https://www.spigotmc.org/resources/beautyindicator-entity-health-in-combat.57546/download?version=225018/SupportTheAuthor');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, version, author, language, address, download) VALUES (8, '粒子图像', 'Powder', '搬运', '1.12', 'StupidDr', '简体中文|English', 'https://www.spigotmc.org/resources/powder.57227/', 'https://www.spigotmc.org/resources/powder.57227/download?version=224643');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, version, author, language, address, download) VALUES (9, 'SkillAPI教程and案例——来自定义职业吧！', 'SkillAPI Tutorial and Case Study - Customize Your Career!', '原创', '无', 'TuringICE', '简体中文', 'https://www.mcbbs.net/thread-809466-1-1.html', 'https://www.mcbbs.net/thread-809466-1-1.html');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, version, author, language, address, download) VALUES (10, '高科技电力', 'Advanced Electricity', '搬运', '1.10-1.12', '作者', '简体中文|English', 'https://www.spigotmc.org/resources/advanced-electricity.56514/', 'https://www.spigotmc.org/resources/advanced-electricity.56514/download?version=221252');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, version, author, language, address, download) VALUES (11, '维护模式', 'MaintenanceMode', '搬运', '1.8-1.12', 'kennytv', '简体中文|English', 'https://www.spigotmc.org/resources/maintenancemode-bungee-and-spigot-support.40699/', 'https://www.spigotmc.org/resources/maintenancemode-bungee-and-spigot-support.40699/download?version=217708');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, version, author, language, address, download) VALUES (12, '多种语言', 'Language Barrier Breaker', '搬运', '1.8-1.12', '作者', '简体中文|English', 'https://www.spigotmc.org/resources/rosetta-stone-language-barrier-breaker.55570/', 'https://www.spigotmc.org/resources/rosetta-stone-language-barrier-breaker.55570/download?version=217291');
