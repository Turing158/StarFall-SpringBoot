INSERT INTO starfall.notice (id, content) VALUES (1, '测试中！后台不会透露任何隐私');
INSERT INTO starfall.notice (id, content) VALUES (2, '欢迎来到星辰倾城StarFall主页');
INSERT INTO starfall.notice (id, content) VALUES (3, '喜欢像素类游戏，不妨试试我的世界');
INSERT INTO starfall.notice (id, content) VALUES (4, '本项目仅用于本人练手，不要乱搞哦！');


INSERT INTO starfall.user (user, password, name, gender, email, birthday, exp, level, avatar) VALUES ('admin', 'b9827fc7ca8d1d8e9901aeede62a3c69', '管理员123', 1, 'admin@sf.com', '2024-03-13', 40, 999, '20240515192528737981500admin.png');
INSERT INTO starfall.user (user, password, name, gender, email, birthday, exp, level, avatar) VALUES ('qweqwe', 'c38d41808a64fefb0f5f8ea76beafa2a', '新用户2024323', 2, '15818961209@163.com', '2024-03-23', 0, 1, 'default.png');
INSERT INTO starfall.user (user, password, name, gender, email, birthday, exp, level, avatar) VALUES ('StarFall', 'StarFall', 'StarFall官方', null, null, null, null, null, 'default.png');
INSERT INTO starfall.user (user, password, name, gender, email, birthday, exp, level, avatar) VALUES ('test', '2679a5a3e44284f50cc484d196d52ee4', '测试账号', 1, 'test@test.com', '2024-03-21', 45, 6, 'default.png');


INSERT INTO starfall.chat_notice (from_user, to_user, date, content) VALUES ('StarFall', 'admin', '2024-04-10 00:53:00', '测试消息1[&divide&]测试消息2');
INSERT INTO starfall.chat_notice (from_user, to_user, date, content) VALUES ('StarFall', 'admin', '2024-04-10 00:56:15', '测试消息3');
INSERT INTO starfall.chat_notice (from_user, to_user, date, content) VALUES ('test', 'admin', '2024-04-10 16:22:48', '测试消息4');
INSERT INTO starfall.chat_notice (from_user, to_user, date, content) VALUES ('StarFall', 'admin', '2024-04-10 17:56:15', '测试消息5[&divide&]测试消息999[&divide&]测试消息000[&divide&]测试消息888');
INSERT INTO starfall.chat_notice (from_user, to_user, date, content) VALUES ('test', 'admin', '2024-04-10 19:22:48', '测试消息6');
INSERT INTO starfall.chat_notice (from_user, to_user, date, content) VALUES ('admin', 'test', '2024-04-11 08:29:10', '测试消息11');
INSERT INTO starfall.chat_notice (from_user, to_user, date, content) VALUES ('admin', 'StarFall', '2024-04-10 08:29:25', '测试消息12[&divide&]测试消息13');
INSERT INTO starfall.chat_notice (from_user, to_user, date, content) VALUES ('admin', 'test', '2024-05-12 00:56:19', 'asdasdasdasd');
INSERT INTO starfall.chat_notice (from_user, to_user, date, content) VALUES ('admin', 'test', '2024-05-12 01:02:03', 'asdasdasdasdasd[&divide&]asdasdasdggggg');



INSERT INTO starfall.sign_in (user, date, message, emotion) VALUES ('admin', '2024-04-04', '好', '开心');
INSERT INTO starfall.sign_in (user, date, message, emotion) VALUES ('admin', '2024-04-03', '不好', '开心');
INSERT INTO starfall.sign_in (user, date, message, emotion) VALUES ('admin', '2024-04-02', '非常不好', '郁闷');
INSERT INTO starfall.sign_in (user, date, message, emotion) VALUES ('admin', '2024-03-22', '无敌好', '伤心');
INSERT INTO starfall.sign_in (user, date, message, emotion) VALUES ('admin', '2024-04-01', '好不好我不知道', '开心');
INSERT INTO starfall.sign_in (user, date, message, emotion) VALUES ('admin', '2024-03-13', '随便', '郁闷');
INSERT INTO starfall.sign_in (user, date, message, emotion) VALUES ('admin', '2024-03-14', '得了', '伤心');
INSERT INTO starfall.sign_in (user, date, message, emotion) VALUES ('admin', '2024-03-14', 'omg', '开心');
INSERT INTO starfall.sign_in (user, date, message, emotion) VALUES ('admin', '2024-03-16', 'emotional damage', '郁闷');
INSERT INTO starfall.sign_in (user, date, message, emotion) VALUES ('admin', '2024-03-17', 'wtf', '伤心');
INSERT INTO starfall.sign_in (user, date, message, emotion) VALUES ('admin', '2024-03-18', 'wtf', '开心');
INSERT INTO starfall.sign_in (user, date, message, emotion) VALUES ('admin', '2024-03-19', '非常好', '郁闷');
INSERT INTO starfall.sign_in (user, date, message, emotion) VALUES ('admin', '2024-03-20', '无敌好', '伤心');
INSERT INTO starfall.sign_in (user, date, message, emotion) VALUES ('admin', '2024-04-12', '非常好！！！', '小丑');
INSERT INTO starfall.sign_in (user, date, message, emotion) VALUES ('test', '2024-04-13', '无事发生', '无聊');
INSERT INTO starfall.sign_in (user, date, message, emotion) VALUES ('admin', '2024-04-15', '666', '小丑');


INSERT INTO starfall.topic (id, title, label, user, date, view, comment, version) VALUES (1, '[1.8.x-1.9.x][Spigot]StarFall空岛生存>巨大更新[物品扩展|粘液科技]', '服务端', 'admin', '2023-11-12', 105, 12, '1.8.x-1.9.');
INSERT INTO starfall.topic (id, title, label, user, date, view, comment, version) VALUES (2, '[1.12.2-1.8][低配福利]骐的整合---纯净基础整合[持续更新]', '客户端', 'admin', '2023-07-04', 100, 8, '1.12.2-1.8');
INSERT INTO starfall.topic (id, title, label, user, date, view, comment, version) VALUES (3, '[1.8.x][Spigot]星辰倾城-起床战争服务端', '服务端', 'admin', '2023-07-02', 100, 3, '1.8.x');
INSERT INTO starfall.topic (id, title, label, user, date, view, comment, version) VALUES (4, '[冰骐解说]我的世界|亡灵战争', '视频', 'admin', '2023-09-19', 100, 6, '1.6');
INSERT INTO starfall.topic (id, title, label, user, date, view, comment, version) VALUES (5, '[冰骐]Minecraft双人默契大挑战——两位初三的帅[dou]气[bi]解密系列', '视频', 'admin', '2023-06-14', 100, 6, '无');
INSERT INTO starfall.topic (id, title, label, user, date, view, comment, version) VALUES (7, '[信息]BeautyIndicator——轻量级的显血插件[1.8-1.12] [接权搬运]', '插件', 'admin', '2023-09-16', 100, 3, '1.8-1.12');
INSERT INTO starfall.topic (id, title, label, user, date, view, comment, version) VALUES (8, '[娱乐|信息][StarMC]Powder——通过粒子来显示图像！[1.12][接权搬运]', '插件', 'admin', '2023-09-19', 100, 1, '1.12');
INSERT INTO starfall.topic (id, title, label, user, date, view, comment, version) VALUES (9, 'SkillAPI教程and案例——来自定义职业吧！', '文章', 'admin', '2023-09-19', 100, 8, '无');
INSERT INTO starfall.topic (id, title, label, user, date, view, comment, version) VALUES (10, '[娱乐|机制]Advanced Electricity——高科技电力[接权搬运][1.10-1.12]', '插件', 'admin', '2023-07-03', 100, 9, '1.10-1.12');
INSERT INTO starfall.topic (id, title, label, user, date, view, comment, version) VALUES (11, '[管理|信息][StarMC]MaintenanceMode-维护模式[接权搬运][1.8-1.12]', '插件', 'admin', '2023-09-19', 100, 4, '1.8-1.12');
INSERT INTO starfall.topic (id, title, label, user, date, view, comment, version) VALUES (12, '[信息]Language Barrier Breaker-多种语言[接权搬运][1.8-1.12] ', '插件', 'admin', '2023-07-05', 100, 2, '1.8-1.12');
INSERT INTO starfall.topic (id, title, label, user, date, view, comment, version) VALUES (13, '简直无子敌啦孩子', '插件', 'test', '2024-04-05', 2, 0, 't简简简简');



INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (2, 'admin', 1, '2023-08-29 00:00:00');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (3, 'admin', 2, '2023-08-29 00:00:00');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (5, 'admin', 2, '2023-08-29 00:00:00');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (9, 'admin', 1, '2024-04-10 21:32:44');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (11, 'admin', 1, '2023-08-29 00:00:00');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (1, 'test', 1, '2023-08-29 00:00:00');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (2, 'test', 2, '2023-08-29 00:00:00');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (3, 'test', 1, '2023-08-29 00:00:00');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (5, 'test', 2, '2023-08-29 00:00:00');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (8, 'test', 2, '2023-08-29 00:00:00');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (10, 'test', 1, '2023-08-29 00:00:00');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (11, 'test', 1, '2023-08-29 00:00:00');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (1, 'admin', 1, '2024-04-02 13:35:29');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (4, 'admin', 1, '2023-09-19 00:00:00');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (7, 'admin', 2, '2024-03-30 16:44:43');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (10, 'admin', 1, '2024-04-02 13:26:29');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (13, 'admin', 0, '2024-04-05 16:23:15');
INSERT INTO starfall.likelog (topicId, user, status, date) VALUES (12, 'admin', 0, '2024-04-10 21:30:00');




INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (1, 'StarFall空岛生存', 'StarFall-Skyblock', '原创', 'TuringICE', '简体中文', 'https://www.mcbbs.net/thread-792740-1-1.html', 'http://某.盘.com', '# <center>?️<font color="darkblue">星辰倾城</font>-<font color="darkcyan">空岛生存</font>?️</center>
# <center><font color="darkblue">StarFall</font>-<font color="darkcyan">skyblock</font></center>

* * *

> ## <center>✉️介绍✉️</center>
<center><font color="darkred"><font size="4">

> **重大更新**

</font></font></center>

> <font color="green"><font size="2"><center>1.9版本更新换代,插件重整,共53个模组,更加流畅,少报错,启动快,完全启动只需15-20秒[虽然看起来有点慢,可是可以进服了]</center></font></font>

> <font color="green"><font size="2"><center>总体来说这次更新是大更新,加了扩展,不仅只有粘液科技啦！</center></font></font>

> <font color="darkred"><font size="2"><center>PS:本服务端已经做好标记,请不要随意改端后发出,否则后果自负</center></font></font>

> <font color="darkred"><font size="2"><center>最后,请自行将ceshi ban了,或者删除,ID密码为888888</center></font></font>

* * *

> ## <center>?开服证明?</center>
![可输入文字](https://attachment.mcbbs.net/data/myattachment/forum/202008/29/224619um2nanm22li22wzw.png.thumb.jpg)

* * *

> ## <center>⬇️下载⬇️</center>
<center>

>[UC](https://www.mcbbs.net/plugin.php?id=link_redirect&target=https%3A%2F%2Fwww.yun.cn%2Fs%2F0b98e12098fb41a6a72919a96101fe6c):IH2S====[坚果云](https://www.mcbbs.net/plugin.php?id=link_redirect&target=https%3A%2F%2Fwww.jianguoyun.com%2Fp%2FDS_j64UQg-zgCBjHm7kD):starfall====[微云](https://www.mcbbs.net/plugin.php?id=link_redirect&target=https%3A%2F%2Fshare.weiyun.com%2F2XfKdZIl):SFSKYB====[天翼](https://www.mcbbs.net/plugin.php?id=link_redirect&target=https%3A%2F%2Fcloud.189.cn%2Ft%2FuiqaeavEnUbi):3kqz====[百度网盘](https://www.mcbbs.net/plugin.php?id=link_redirect&target=https%3A%2F%2Fstarfalls.lanzous.com%2Fb00nvca2j):SFSB
>[蓝奏云](https://www.mcbbs.net/plugin.php?id=link_redirect&target=https%3A%2F%2Fstarfalls.lanzous.com%2Fb00nvca2j):sfsb====[115](https://www.mcbbs.net/plugin.php?id=link_redirect&target=https%3A%2F%2F115.com%2Fs%2Fsw3tsjr3hc9):ne89

</center>

* * *

> ## <center>❓Q&A❗</center>
**<font color="red">Q：服务端可以用作服务器吗？</font>**
<font color="green">A：可以,但是要注明作者！</font>

><font color="red">Q：菜单的配置可以用吗？</font>
<font color="green">A：可以,可以不用注明作者！</font>

><font color="red">Q：地图可以拿走吗？</font>
<font color="green">A：建议去原贴拿！[正在找地图原贴]</font>1
');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (2, '骐的整合', 'Integration of Qi', '原创', '作者', '简体中文', 'https://www.mcbbs.net/thread-1126142-1-1.html', 'https://www.mcbbs.net/thread-1126142-1-1.html', '# 骐的整合

#### 1.骐的整合☺
骐的整合
无mybatis，没学

#### 2.骐的整合☺
骐的整合
基于ssm框架，但是没mybatis

#### 3.安装教程☺

1.  安装配置MySQL 5.5以上数据库系统
2.  连接数据库,创建 login_web 数据库导入doc/web_user

#### 4.骐的整合☺

1. 下载tomcat8以上版本
配置tomcat，方可运行
');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (3, '星辰倾城-起床战争服务端', 'StarFall-BedWard', '原创', '作者', '简体中文', 'https://www.mcbbs.net/thread-773917-1-1.html', 'http://www.本贴.com', '# 星辰倾城-起床战争服务端

#### 1.星辰倾城-起床战争服务端☺
星辰倾城-起床战争服务端
无mybatis，没学

#### 2.星辰倾城-起床战争服务端☺
星辰倾城-起床战争服务端
基于ssm框架，但是没mybatis

#### 3.安装教程☺

1.  安装配置MySQL 5.5以上数据库系统
2.  连接数据库,创建 login_web 数据库导入doc/web_user

#### 4.星辰倾城-起床战争服务端☺

1. 星辰倾城-起床战争服务端
配置tomcat，方可运行
');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (4, '我的世界|亡灵战争', 'Minecraft|War of the Undead', '原创', 'TuringICE', '简体中文', 'https://www.mcbbs.net/thread-878770-1-1.html', 'https://www.mcbbs.net/thread-878770-1-1.html', '![可输入文字](https://attachment.mcbbs.net/data/myattachment/forum/201906/29/183141q25r7p7i2zgreqd2.jpg.thumb.jpg)
# <font color="darkblue"><center>[冰骐解说]我的世界|亡灵战争系列</center></font>
* * *
### <center><font color="green">爱奇艺</font></center>
第一集:[点我](https://www.iqiyi.com%2Fv_19rseaqzv4.html%23vfrm%3D8-8-0-1)
第二集:[点我](https://www.iqiyi.com%2Fv_19rsev8now.html%23vfrm%3D8-8-0-1)
~~第三集:点我[上传中]~~


* * *
### <center><font color="blue">优酷</font></center>

[第一集](https://player.youku.com/embed/XNDI0NTkzNTk0MA==)
[第二集](https://player.youku.com/embed/XNDI0NTkzNTk0MA==)
[第三集](https://player.youku.com/embed/XNDI1MTI2MDg1Mg==)

* * *
### <center><font color="orange">虎牙</font></center>


第一集:[点我](https://v.huya.com%2Fplay%2F175958297.html)
第二集:[点我](https://v.huya.com%2Fplay%2F176468657.html)
~~第三集:点我[未上传]~~


* * *
### <center><font color="lightblue">bilibili</font></center>

[第一集](https://www.bilibili.com/video/BV1Ex411X7BZ?t=14.3)
[第二集](https://www.bilibili.com/video/BV1Ex411X7y7?t=22.4)
[第三集](https://www.bilibili.com/video/BV1hx411X77f?t=28.9)

* * *

**<font size="5"><font color="darkred"><center>记得点关注</center></font></font>**
<center>Q:1322621134    微博:冰骐BQIT    粉丝群:466252736</center>

地图下载地址:<a href="http://www.mcbbs.net/thread-467098-1-1.html">点</a>

有啥做的不好的可以在下面来评论哦！
<font size="2">这，真是我见过的，最美的早晨。终于，我们迎来了短暂的和平。但，我的使命，仅仅完成了一半。</font>
<div style="float:right;"><font size="2">——布莱克</font></div>
');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (5, '双人默契大挑战', 'The Great Challenge of Mutual Understanding', '原创', '作者', '简体中文', 'https://www.mcbbs.net/thread-812503-1-1.html', 'https://www.mcbbs.net/thread-812503-1-1.html', '1');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (7, '轻量级的显血', 'BeautyIndicator', '搬运', 'haelexuis', '简体中文|English', 'https://www.spigotmc.org/resources/beautyindicator-entity-health-in-combat.57546/', 'https://www.spigotmc.org/resources/beautyindicator-entity-health-in-combat.57546/download?version=225018/SupportTheAuthor', '# <center><font color="darkblue">Beauty</font><font color="purple">Indicator</font></center>
# <center><font color="darkblue">显血</font></center>

* * *

> ### <center>简介</center>
> <center>简单且完全可自定义的显血插件,并且轻量级哦[可配置].</center>
> <center>这个插件的配置绝对连小白腐竹都会</center>

* * *
> ### <center>展示截图</center>
![可输入文字](https://www.spigotmc.org/attachments/chrome_2018-06-12_03-19-48-png.348027/)

* * *

> ### <center>指令&权限</center>
<table>
<tr>
<th>指令</th>
<th>详细</th>
<th>权限</th>
</tr>
<tr>
<td><center><font color="green">/beautyindicator</font></center></td>
<td><center>重新加载插件配置</center></td>
<td><center><font color="red">beautyindicator.reload</font></center></td>
</tr>
</table>

> <font size="1"><center>指令就这一个[果然真是轻量级的,牛]</center></font>

* * *

> ### <center>配置</center>
[<center>下载</center>](https://www.mcbbs.net/forum.php?mod=attachment&aid=MTIwODQxM3wwY2M2NmFhNXwxNjk0ODYzMTE2fDE1NjQ2NjV8Nzk4MjY0)

* * *

> ### <center>下载</center>
[<center>原贴</center>](https://www.spigotmc.org/resources/beautyindicator-entity-health-in-combat.57546/)[<center>下载</center>](https://www.spigotmc.org/resources/beautyindicator-entity-health-in-combat.57546/download?version=225018/SupportTheAuthor)

* * *

> ### <center>转载证明</center>
![可输入文字](https://attachment.mcbbs.net/data/myattachment/forum/201806/08/234137bnnykiuyjh3zneyn.jpg.thumb.jpg)
');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (8, '粒子图像', 'Powder', '搬运', 'StupidDr', '简体中文|English', 'https://www.spigotmc.org/resources/powder.57227/', 'https://www.spigotmc.org/resources/powder.57227/download?version=224643', '**<font size="7"><center><font color="darkgoldenrod">P</font><font color="green">o</font><font color="purple">w</font><font color="red">d</font><font color="darkcyan">e</font>r</center></font>**
<center><font size="5"><font color="green">粒</font><font color="purple">子</font><font color="orange">图</font><font color="darkcyan">像</font></font></center>

* * *

> ### <center><font color="purple">简介</font></center>
<table>
<tr>
<td style="width:150px">
Powder是一个Spigot插件[谁都知道啦!]，它利用粒子和声音效果来实现自定义的图片和动画.
</td>
<td>
Powder可以作为子插件来使用[比如SimplePets&LibsDisguises]，可以用来代替全息[比如HolographicDisplays]，还可以用来装饰世界服务器.Powder效果在Powders.yml文件中创建，可以使用声音和粒子。可以导入图像来创建Powder，注意可以导入歌曲到Block Studio文件以创建歌曲
</td>
<td style="width:360px">
<font color="green">无限制地创建Powder中的动画</font>

<font color="purple">可改变动画的旋转/声音</font>

<font color="green">在游戏中使用任何声音或粒子</font>

<font color="purple">允许Powder跟随玩家的方向</font>

<font color="green">改变Powder粒子之间的间距</font>

<font color="purple">Powder可跟随到玩家和实体</font>

<font color="green">可添加到指定位置[在配置文件里,详情WIKI]</font>

<font color="purple">MySQL-允许在服务器注销和登录后使用Powders</font>

<font color="green">可从URL或本地文件中导入图片来创建Powder</font>

<font color="purple">在某个Powder中,将音乐添加到Powder中时,可以使粒子可以以歌曲的旋律&节拍出现</font>
</td>
</tr>
</table>

![可输入文字](https://proxy.spigotmc.org/1a5063caa97af1b01980448f8de68ec4fb8b69a1?url=https%3A%2F%2Fi.imgur.com%2F24nIC2r.png)

* * *

> ### <center><font color="purple">指令&权限</font></center>
<table>
<tr>
<td>
/powder
主指令
</td>
<td>
/powder help
查看帮助
</td>
<td>
/powder list
查看所有的Powder
</td>
<td>
/powder search
搜索创建过的Powder
</td>
<td>
/powder nearby
[理解中……]
</td>
<td style="width:130px">
/powder reload
重新加载
</td>
</tr>
</table>

* * *

> ### <center><font color="darkbule">权限&使用方法</font></center>
[<center>WIKI</center>](https://github.com/Ruinscraft/Powder/wiki/Commands-and-permissions)

* * *

> ### <center><font color="purple">原帖&下载</font></center>
[<center>原帖</center>](https://www.spigotmc.org/resources/powder.57227/)[<center>下载</center>](https://www.spigotmc.org/resources/powder.57227/download?version=224643)




');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (9, 'SkillAPI教程and案例——来自定义职业吧！', 'SkillAPI Tutorial and Case Study - Customize Your Career!', '原创', 'TuringICE', '简体中文', 'https://www.mcbbs.net/thread-809466-1-1.html', 'https://www.mcbbs.net/thread-809466-1-1.html', '# <center><font color="darkblue">Skill</font><font color="darkred">API</font></center>
# <center><font color="darkgreen">教程</font></center>

* * *

> [<center>SkillAPI编辑器[中文]</center>](https://www.mcbbs.net/plugin.php?id=link_redirect&target=https%3A%2F%2Fantarctics.github.io%2FSkillAPI%2F)[<center>SkillAPI官方编辑器[英文]</center>](https://www.mcbbs.net/plugin.php?id=link_redirect&target=http%3A%2F%2Feniripsa96.github.io%2FSkillAPI%2F)
> <center>|看得懂用这个,看不懂...|</center>

> [<center>经验换算器</center>](https://www.mcbbs.net/plugin.php?id=link_redirect&target=https%3A%2F%2Fantarctics.github.io%2FSkillAPI%2Fexp.html%2F)


* * *
> ### <center>SkillAPI插件介绍及下载帖</center>
<center>

> [[HAYO Studio]](http://www.mcbbs.net/thread-314419-1-1.html)
and
[[狗屎君]](http://www.mcbbs.net/thread-804492-1-1.html)

</center>

* * *

> ### <center>视频</center>
[<center>跳转</center>](https://www.bilibili.com/video/av28044290?t=86.2)
* * *

> ### <center>其他</center>
<font size="1"><center>因为我要进入初三了
学业繁忙
这个帖子和视频有什么问题直接在下面评论就行了
还有什么不明白的加我Q:1322621134
虽然我也不是很懂
听不懂,找别人吧！</center></font>');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (10, '高科技电力', 'Advanced Electricity', '搬运', '作者', '简体中文|English', 'https://www.spigotmc.org/resources/advanced-electricity.56514/', 'https://www.spigotmc.org/resources/advanced-electricity.56514/download?version=221252', '1');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (11, '维护模式', 'MaintenanceMode', '搬运', 'kennytv', '简体中文|English', 'https://www.spigotmc.org/resources/maintenancemode-bungee-and-spigot-support.40699/', 'https://www.spigotmc.org/resources/maintenancemode-bungee-and-spigot-support.40699/download?version=217708', '<center><font size="7"><font color="darkred">MaintenanceMode</font></font>

<font size="6"><font color="red">维护模式</font></font></center>

> ### **<font color="purple"><center>介绍</center></font>**
><center><font size="2" color="darkgoldenrod">这个插件可以让BungeeCord.Spigot.Bukkit服务器进入维护模式，可以禁止玩家加入服务器。
您可以选择要维护多长时间或启动一个计时器</font></center>
><center><font size="2" color="darkgoldenrod">消息是可定义的，因此您可以自定义在线玩家数[可随意更改]和服务器图标[将图片放在插件文件中，并且改为</font></center>

* * *

> ### **<font color="purple"><center>指令|权限</center></font>**
/ maintenance <on / off>[想要设置维护模式的状态]
/maintenance reload[可让您在服务器运行时重新加载配置文件]
/ maintenace <add / remove> <玩家>[允许特定玩家加入服务器，即使启用了维护]
/maintenance whitelist[返回维护白名单上所有玩家的列表]
/maintenance timer starttimer <时间分钟>[时间到后启用维护模式]
/ maintenance timer endtimer <时间分钟>[在设定的时间中,时间到了,它会结束维护模式]
/ maintenance update[更新插件]

>权限
maintenance.admin - 使用上面列出的维护指令
maintenance.reload - 使用“/ maintenance reload”指令
maintenance.bypass - 让您绕过维护模式
maintenance.joinnotification - 如果启用：如果玩家在启用维护的情况下尝试加入服务器，则向您发送通知

* * *

> ### **<font color="purple"><center>下载</center></font>**
>[<center>原贴</center>](https://www.spigotmc.org/resources/maintenancemode-bungee-and-spigot-support.40699/)[<center>下载</center>](https://www.spigotmc.org/resources/maintenancemode-bungee-and-spigot-support.40699/download?version=217708)

* * *

![转载](https://attachment.mcbbs.net/data/myattachment/forum/201804/22/210650a401snzru10rc2rr.jpg.thumb.jpg)');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (12, '多种语言', 'Language Barrier Breaker', '搬运', '作者', '简体中文|English', 'https://www.spigotmc.org/resources/rosetta-stone-language-barrier-breaker.55570/', 'https://www.spigotmc.org/resources/rosetta-stone-language-barrier-breaker.55570/download?version=217291', '1');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (13, '简简简简简简简简', 'topic/detail', '原创', 'to简简简简', 'topic/detail简简简简', 'top简简简简', 'topi简简简简', '简直无敌啦孩子tail/15topic/detail/15topic/detail/简直无敌啦孩子简简简简');




INSERT INTO starfall.comment (topicId, user, date, content) VALUES (9, 'admin', '2023-10-16 00:00:00', '测试评论-683537485');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (4, 'admin', '2023-03-14 00:00:00', '测试评论225783113');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'admin', '2023-06-19 00:00:00', '测试评论1770570008');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'admin', '2023-07-10 00:00:00', '测试评论-1100647895');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (6, 'admin', '2022-12-22 00:00:00', '测试评论-456845751');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (10, 'admin', '2022-12-21 00:00:00', '测试评论-462122861');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'admin', '2022-12-26 00:00:00', '测试评论-1743143726');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (7, 'admin', '2023-02-01 00:00:00', '测试评论-184619200');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (1, 'admin', '2023-01-21 00:00:00', '测试评论886283546');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'admin', '2023-08-23 00:00:00', '测试评论-143431205');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (8, 'admin', '2023-05-03 00:00:00', '测试评论555413033');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (6, 'admin', '2023-05-15 00:00:00', '测试评论1615656574');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'admin', '2023-11-22 00:00:00', '测试评论740442019');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (7, 'admin', '2023-07-17 00:00:00', '测试评论-1014242');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (9, 'admin', '2023-04-30 00:00:00', '测试评论432146530');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (1, 'admin', '2022-12-23 00:00:00', '测试评论1420787184');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (6, 'admin', '2023-01-09 00:00:00', '测试评论365128670');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'admin', '2023-06-04 00:00:00', '测试评论-292489144');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (2, 'admin', '2022-12-19 00:00:00', '测试评论104795894');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (7, 'admin', '2023-03-16 00:00:00', '测试评论726984385');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (8, 'admin', '2023-01-16 00:00:00', '测试评论-713124235');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (6, 'admin', '2023-05-18 00:00:00', '测试评论511866430');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'admin', '2023-04-22 00:00:00', '测试评论1041097363');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (8, 'admin', '2023-10-07 00:00:00', '测试评论314113989');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (4, 'admin', '2023-04-03 00:00:00', '测试评论-467251855');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (1, 'admin', '2023-03-04 00:00:00', '测试评论292774849');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (2, 'admin', '2023-05-27 00:00:00', '测试评论423402271');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (1, 'admin', '2023-01-11 00:00:00', '测试评论-1284233472');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (9, 'admin', '2023-08-12 00:00:00', '测试评论83979065');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (9, 'admin', '2023-04-10 00:00:00', '测试评论520382576');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (6, 'admin', '2023-07-19 00:00:00', '测试评论1513617735');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (12, 'admin', '2023-06-19 00:00:00', '测试评论-797846303');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (8, 'admin', '2023-02-03 00:00:00', '测试评论-316104427');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (8, 'admin', '2023-10-14 00:00:00', '测试评论-739014682');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'admin', '2022-12-08 00:00:00', '测试评论-191954674');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (9, 'admin', '2023-11-04 00:00:00', '测试评论2004188408');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (4, 'admin', '2023-03-10 00:00:00', '测试评论196010549');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (4, 'admin', '2023-05-18 00:00:00', '测试评论-423328936');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (1, 'admin', '2023-04-23 00:00:00', '测试评论-46743787');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (9, 'admin', '2022-12-04 00:00:00', '测试评论1212928547');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (1, 'admin', '2023-02-12 00:00:00', '测试评论2030862991');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (12, 'admin', '2023-07-13 00:00:00', '测试评论-1070355816');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (11, 'admin', '2023-01-23 00:00:00', '测试评论-911608736');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (6, 'admin', '2023-09-25 00:00:00', '测试评论-1389942835');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (8, 'admin', '2023-11-02 00:00:00', '测试评论-1287163858');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'admin', '2023-07-31 00:00:00', '测试评论-2079762997');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (2, 'admin', '2023-11-03 00:00:00', '测试评论-987445842');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (4, 'admin', '2023-07-27 00:00:00', '测试评论-1428750558');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (12, 'admin', '2023-01-09 00:00:00', '测试评论-2019751943');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (9, 'admin', '2023-11-08 00:00:00', '测试评论-1064165994');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (7, 'admin', '2023-04-06 00:00:00', '测试评论-569317786');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'admin', '2023-11-23 00:00:00', '测试评论1720092988');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'admin', '2023-09-01 00:00:00', '测试评论1439157248');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (9, 'admin', '2023-10-17 00:00:00', '测试评论285219133');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'admin', '2023-04-12 00:00:00', '测试评论-843164495');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'admin', '2023-04-13 00:00:00', '测试评论-583960679');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (2, 'admin', '2023-06-02 00:00:00', '测试评论-626274608');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'admin', '2023-07-10 00:00:00', '测试评论-1521444866');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (7, 'admin', '2023-05-17 00:00:00', '测试评论-1044811666');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (12, 'admin', '2023-01-13 00:00:00', '测试评论223403833');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'admin', '2023-11-18 00:00:00', '测试评论1374096879');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (11, 'admin', '2023-05-19 00:00:00', '测试评论601083369');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'admin', '2023-01-18 00:00:00', '测试评论-521394724');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (8, 'admin', '2023-10-09 00:00:00', '测试评论1684873845');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (2, 'admin', '2023-06-07 00:00:00', '测试评论771577336');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (9, 'admin', '2023-07-14 00:00:00', '测试评论1756538431');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'admin', '2023-02-21 00:00:00', '测试评论583765392');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (4, 'admin', '2023-04-04 00:00:00', '测试评论-127985127');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'admin', '2023-01-23 00:00:00', '测试评论177443095');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'admin', '2023-06-08 00:00:00', '测试评论-1806277233');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (8, 'admin', '2023-06-22 00:00:00', '测试评论647449275');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (10, 'admin', '2022-12-31 00:00:00', '测试评论317180177');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'admin', '2023-04-16 00:00:00', '测试评论312878132');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (7, 'admin', '2023-07-04 00:00:00', '测试评论-1487028499');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (1, 'admin', '2023-03-08 00:00:00', '测试评论-204428931');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (8, 'admin', '2023-04-04 00:00:00', '测试评论-1548992318');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (7, 'admin', '2023-01-07 00:00:00', '测试评论1157339420');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (7, 'admin', '2023-10-05 00:00:00', '测试评论-227754172');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (4, 'admin', '2023-04-16 00:00:00', '测试评论209633680');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'admin', '2023-07-02 00:00:00', '测试评论-1026452404');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (2, 'admin', '2023-07-18 00:00:00', '测试评论600069309');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (7, 'admin', '2023-03-16 00:00:00', '测试评论-813694141');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (10, 'admin', '2023-08-14 00:00:00', '测试评论-1004657570');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'admin', '2023-04-25 00:00:00', '测试评论541545347');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (11, 'test', '2023-10-21 00:00:00', '测试评论-1278987535');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (8, 'test', '2023-06-22 00:00:00', '测试评论-2082622192');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (8, 'test', '2023-02-02 00:00:00', '测试评论431457578');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (2, 'test', '2023-06-08 00:00:00', '测试评论-454834539');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (4, 'test', '2023-08-27 00:00:00', '测试评论-1181714959');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (6, 'test', '2023-06-22 00:00:00', '测试评论-1314377889');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'test', '2023-07-13 00:00:00', '测试评论-1477614622');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (4, 'test', '2023-01-23 00:00:00', '测试评论-1694155389');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (7, 'test', '2023-09-25 00:00:00', '测试评论-108752644');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (7, 'test', '2023-11-02 00:00:00', '测试评论708545771');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (9, 'test', '2023-07-31 00:00:00', '测试评论1988405724');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (10, 'test', '2023-03-14 00:00:00', '测试评论1548846001');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'test', '2023-06-19 00:00:00', '测试评论-1088811987');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (7, 'test', '2023-07-10 00:00:00', '测试评论-855785553');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (10, 'test', '2022-12-22 00:00:00', '测试评论895320158');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (10, 'test', '2022-12-21 00:00:00', '测试评论810699094');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'test', '2022-12-26 00:00:00', '测试评论-1274520938');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (8, 'test', '2023-05-18 00:00:00', '测试评论929537982');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'test', '2023-04-22 00:00:00', '测试评论-1634388252');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (11, 'test', '2023-10-07 00:00:00', '测试评论-1734086145');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (10, 'test', '2023-04-03 00:00:00', '测试评论-1640774469');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (9, 'test', '2023-03-04 00:00:00', '测试评论-248768066');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'test', '2023-10-09 00:00:00', '测试评论-1972808292');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (4, 'test', '2023-05-27 00:00:00', '测试评论-2016335094');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (10, 'test', '2023-01-11 00:00:00', '测试评论-1549851883');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (9, 'test', '2023-08-12 00:00:00', '测试评论-1415139992');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (1, 'test', '2023-04-10 00:00:00', '测试评论-1267040279');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (6, 'test', '2023-05-19 00:00:00', '测试评论-694496724');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'test', '2023-01-18 00:00:00', '测试评论-1544918896');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (2, 'test', '2023-10-09 00:00:00', '测试评论-668255222');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'test', '2023-06-07 00:00:00', '测试评论-258398495');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (9, 'test', '2023-07-14 00:00:00', '测试评论-622449684');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (11, 'test', '2023-02-21 00:00:00', '测试评论-567804659');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (9, 'test', '2023-07-02 00:00:00', '测试评论-233756923');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (2, 'test', '2023-10-20 00:00:00', '测试评论1753615311');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (10, 'test', '2023-07-18 00:00:00', '测试评论818637349');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'test', '2023-03-16 00:00:00', '测试评论1762846689');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (1, 'test', '2023-03-17 00:00:00', '测试评论-627851698');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (8, 'test', '2023-08-14 00:00:00', '测试评论2135986865');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (10, 'test', '2023-04-25 00:00:00', '测试评论-1686207864');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'test', '2023-06-08 00:00:00', '测试评论-84948396');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'test', '2023-08-27 00:00:00', '测试评论-1914568975');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'test', '2023-06-22 00:00:00', '测试评论574416058');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (3, 'test', '2023-07-13 00:00:00', '测试评论554456343');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (2, 'test', '2023-01-23 00:00:00', '测试评论497199651');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (2, 'test', '2023-06-08 00:00:00', '测试评论1240965514');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (5, 'test', '2023-08-27 00:00:00', '测试评论-1571036462');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (8, 'test', '2023-06-22 00:00:00', '测试评论690365099');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (6, 'test', '2022-12-31 00:00:00', '测试评论851676159');
INSERT INTO starfall.comment (topicId, user, date, content) VALUES (1, 'test', '2023-04-16 00:00:00', '测试评论-115771190');
