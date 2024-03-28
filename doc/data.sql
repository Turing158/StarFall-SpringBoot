# user
INSERT INTO starfall.user (user, password, name, gender, email, birthday, exp, level, avatar) VALUES ('admin', 'b9827fc7ca8d1d8e9901aeede62a3c69', '管理员', 3, 'admin@sf.com', '2024-03-02', 10, 999, 'default.png');
INSERT INTO starfall.user (user, password, name, gender, email, birthday, exp, level, avatar) VALUES ('qweqwe', 'c38d41808a64fefb0f5f8ea76beafa2a', '新用户2024323', 2, '15818961209@163.com', '2024-03-23', 0, 1, 'default.png');
INSERT INTO starfall.user (user, password, name, gender, email, birthday, exp, level, avatar) VALUES ('test', '2679a5a3e44284f50cc484d196d52ee4', '测试账号', 1, 'test@test.com', '2024-03-21', 10, 1, 'default.png');




# notice
INSERT INTO starfall.notice (id, content) VALUES (1, '测试中！后台不会透露任何隐私');
INSERT INTO starfall.notice (id, content) VALUES (2, '欢迎来到星辰倾城StarFall主页');
INSERT INTO starfall.notice (id, content) VALUES (3, '喜欢像素类游戏，不妨试试我的世界');
INSERT INTO starfall.notice (id, content) VALUES (4, '本项目仅用于本人练手，不要乱搞哦！');





# topic
INSERT INTO starfall.topic (id, title, label, user, date, view, comment, version) VALUES (1, '[1.8.x-1.9.x][Spigot]StarFall空岛生存>巨大更新[物品扩展|粘液科技]', '服务端', 'admin', '2023-11-12', 100, 12, '1.8.x-1.9.');
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
INSERT INTO starfall.topic (id, title, label, user, date, view, comment, version) VALUES (13, 'test', 'test', 'test', '2024-03-08', 1, 1, '0');



# topicitem
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (1, 'StarFall空岛生存', 'StarFall-Skyblock', '原创', 'TuringICE', '简体中文', 'https://www.mcbbs.net/thread-792740-1-1.html', 'http://某.盘.com', '## 安装

安装地址：[Download | Redis](https://redis.io/download/)或者[Releases · microsoftarchive/redis (github.com)](https://github.com/microsoftarchive/redis/releases)

解压后即可使用

## 配置环境

1. 将下载好的压缩包，解压至一个位置，然后复制该解压的根目录，例如：`E:\\Programmer\\redis`

2. 打开系统环境配置环境变量，找到path双击点开，新建，将复制的路径粘贴进去
3. 剩下的就是保存了

## 使用

- cmd用法
  1. 在安装目录下使用cmd，运行	`redis-server.exe redis.windows.conf`	开启服务
  2. 执行	`redis-cli.exe -h 127.0.0.1 -p 6379`	开启客户端

- 直接使用

  双击“redis-server.exe”则按照默认配置启动Redis服务

  双击“redis-cli.exe”打开客户端控制台可以进行命令操作Redis

## 运用到springboot中

### 添加依赖项

将以下添加到pom.xml里的dependencies标签里

```xml
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>
    </dependencies>
```

### 添加缓存配置文件

1. 在**config**软件包中添加**RedisConfig.java**
2. 继承`CachingConfigurerSupport`类，并添加一个**Bean**管理的`KeyGenerator`方法，方法返回内置的`KeyGenerator`方法
3. 因为是抽象类的方法，所以需要重写方法
4. 将抽象类的形参拼接成字符串并返回
5. 在这个类上用上**@Configuration**注解标注为配置类
6. 在这个类上用上**@EnableCaching**注解启用缓存

```java
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
    @Bean
    public KeyGenerator KeyGenerator(){
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object i : params){
                    sb.append(i.toString());
                }
                return sb.toString();
            }
        };
    }
}
```

### springboot配置文件

其实如果没动过**redis**的配置默认都是这些

但是如果想改，需要在**application.properties**或application.yml中去配置

```properties
# Redis服务器地址
spring.redis.host=localhost
# Redis服务器连接端口
spring.redis.port=6379
# Redis服务器连接密码（默认为空）
spring.redis.password=
# Redis数据库索引 默认:0
spring.redis.database=0
# 连接池最大连接数（使用负值表示没有限制）默认:8
spring.redis.lettuce.pool.max-active=8
# 连接池最大阻塞等待时间（使用负值表示没有限制）默认:-1
spring.redis.lettuce.pool.max-wait=-1
# 连接池中的最大空闲连接 默认:8
spring.redis.lettuce.pool.max-idle=8
# 连接池中的最小空闲连接 默认:0
spring.redis.lettuce.pool.min-idle=0
```

### 使用redis

至此，你就可以愉快的使用**redis**，但是使用**redis**前，记得看看**服务是否打开**！

*没开示例：*

![image-20240313165930244](./Redis的使用.assets/image-20240313165930244.png)

#### 缓存字符串

1. 在使用前，需要通过**@Autowired**注解来注入方法

2. 编写方法，使用`stringRedisTemplate.opsForValue().set(key,value)`来设置字符串



```java
@Service
public class TestService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public String addAValue() {
        stringRedisTemplate.opsForValue().set("test","Hello World");
        String value = stringRedisTemplate.opsForValue().get("test");
        System.out.println(value);
        return value;
    }
}
```

3. *[省略了在控制类运行的编写]*启动**springboot**访问，可以看到返回和控制台的字符串就是**缓存**在redis里的

   ![image-20240313170113687](./Redis的使用.assets/image-20240313170113687.png)

#### 缓存实体类

`StringRedisTemplate`的方法只能用于缓存`String`类型，即**字符串**

而使用`RedisTemplate`，可以缓存**实体类**，即`Object`

1. 在使用前，需要通过**@Autowired**注解来注入方法

2. 在操作之前，需要通过`ValueOperations`来将`redisTemplate.opsForValue()`设置的类型固定为`String, Object`

3. 编写方法，使用`operations.set(key,value)`来设置实体类

   > 这里有一点要注意，实体类的创建需要实现接口类Serializable，就在类后加 `implements Serializable`就行了

```java
@Service
public class TestService {
    @Autowired
    RedisTemplate redisTemplate;

    public String addAEntity() {
        User user = new User("admin","123456","123@456.com",12);
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set("user", user);
        User userObj = (User) operations.get("user");
        System.out.println(userObj);
        return userObj.toString();
    }
}
```

4. *[省略了在控制类运行的编写]*启动**springboot**，可以看到返回和控制台的实体类就是**缓存**在redis里的

   同样的可以通过这样的方法去缓存**int类型，boolean类型**等

   ![image-20240313171420897](./Redis的使用.assets/image-20240313171420897.png)

#### 设置缓存时间

如果不设置缓存时间，那么这个数据将永久存在，除非手动删除，可以通过`stringRedisTemplate.getExpire(key)或RedisTemplate.getExpire(key)`来查看该数据缓存剩余的时间*[过期时间]*
');
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (2, '骐的整合', 'Integration of Qi', '原创', '作者', '简体中文', 'https://www.mcbbs.net/thread-1126142-1-1.html', 'https://www.mcbbs.net/thread-1126142-1-1.html', null);
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (3, '星辰倾城-起床战争服务端', 'StarFall-BedWard', '原创', '作者', '简体中文', 'https://www.mcbbs.net/thread-773917-1-1.html', 'http://www.本贴.com', null);
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (4, '我的世界|亡灵战争', 'Minecraft|War of the Undead', '原创', 'TuringICE', '简体中文', 'https://www.mcbbs.net/thread-878770-1-1.html', 'https://www.mcbbs.net/thread-878770-1-1.html', null);
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (5, '双人默契大挑战', 'The Great Challenge of Mutual Understanding', '原创', '作者', '简体中文', 'https://www.mcbbs.net/thread-812503-1-1.html', 'https://www.mcbbs.net/thread-812503-1-1.html', null);
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (7, '轻量级的显血', 'BeautyIndicator', '搬运', 'haelexuis', '简体中文|English', 'https://www.spigotmc.org/resources/beautyindicator-entity-health-in-combat.57546/', 'https://www.spigotmc.org/resources/beautyindicator-entity-health-in-combat.57546/download?version=225018/SupportTheAuthor', null);
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (8, '粒子图像', 'Powder', '搬运', 'StupidDr', '简体中文|English', 'https://www.spigotmc.org/resources/powder.57227/', 'https://www.spigotmc.org/resources/powder.57227/download?version=224643', null);
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (9, 'SkillAPI教程and案例——来自定义职业吧！', 'SkillAPI Tutorial and Case Study - Customize Your Career!', '原创', 'TuringICE', '简体中文', 'https://www.mcbbs.net/thread-809466-1-1.html', 'https://www.mcbbs.net/thread-809466-1-1.html', null);
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (10, '高科技电力', 'Advanced Electricity', '搬运', '作者', '简体中文|English', 'https://www.spigotmc.org/resources/advanced-electricity.56514/', 'https://www.spigotmc.org/resources/advanced-electricity.56514/download?version=221252', null);
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (11, '维护模式', 'MaintenanceMode', '搬运', 'kennytv', '简体中文|English', 'https://www.spigotmc.org/resources/maintenancemode-bungee-and-spigot-support.40699/', 'https://www.spigotmc.org/resources/maintenancemode-bungee-and-spigot-support.40699/download?version=217708', null);
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (12, '多种语言', 'Language Barrier Breaker', '搬运', '作者', '简体中文|English', 'https://www.spigotmc.org/resources/rosetta-stone-language-barrier-breaker.55570/', 'https://www.spigotmc.org/resources/rosetta-stone-language-barrier-breaker.55570/download?version=217291', null);
INSERT INTO starfall.topicitem (topicId, topicTitle, enTitle, source, author, language, address, download, content) VALUES (13, null, null, null, null, null, null, null, null);




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


#comment
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (9, 'admin', '2023-10-16 00:00:00', '测试评论-683537485');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'admin', '2023-01-09 00:00:00', '测试评论-2012805148');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (4, 'admin', '2023-03-14 00:00:00', '测试评论225783113');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'admin', '2023-06-19 00:00:00', '测试评论1770570008');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'admin', '2023-07-10 00:00:00', '测试评论-1100647895');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (6, 'admin', '2022-12-22 00:00:00', '测试评论-456845751');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (10, 'admin', '2022-12-21 00:00:00', '测试评论-462122861');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'admin', '2022-12-26 00:00:00', '测试评论-1743143726');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (7, 'admin', '2023-02-01 00:00:00', '测试评论-184619200');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (1, 'admin', '2023-01-21 00:00:00', '测试评论886283546');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'admin', '2023-08-23 00:00:00', '测试评论-143431205');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (8, 'admin', '2023-05-03 00:00:00', '测试评论555413033');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'admin', '2023-11-02 00:00:00', '测试评论-1883346768');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (6, 'admin', '2023-05-15 00:00:00', '测试评论1615656574');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'admin', '2023-11-22 00:00:00', '测试评论740442019');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (7, 'admin', '2023-07-17 00:00:00', '测试评论-1014242');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (9, 'admin', '2023-04-30 00:00:00', '测试评论432146530');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (1, 'admin', '2022-12-23 00:00:00', '测试评论1420787184');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (6, 'admin', '2023-01-09 00:00:00', '测试评论365128670');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'admin', '2023-06-04 00:00:00', '测试评论-292489144');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (2, 'admin', '2022-12-19 00:00:00', '测试评论104795894');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (7, 'admin', '2023-03-16 00:00:00', '测试评论726984385');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (8, 'admin', '2023-01-16 00:00:00', '测试评论-713124235');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (1, 'admin', '2022-12-22 00:00:00', '测试评论-1702151857');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (6, 'admin', '2023-05-18 00:00:00', '测试评论511866430');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'admin', '2023-04-22 00:00:00', '测试评论1041097363');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (8, 'admin', '2023-10-07 00:00:00', '测试评论314113989');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (4, 'admin', '2023-04-03 00:00:00', '测试评论-467251855');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (1, 'admin', '2023-03-04 00:00:00', '测试评论292774849');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (12, 'admin', '2023-10-09 00:00:00', '测试评论203167711');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (2, 'admin', '2023-05-27 00:00:00', '测试评论423402271');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (1, 'admin', '2023-01-11 00:00:00', '测试评论-1284233472');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (9, 'admin', '2023-08-12 00:00:00', '测试评论83979065');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (9, 'admin', '2023-04-10 00:00:00', '测试评论520382576');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (6, 'admin', '2023-07-19 00:00:00', '测试评论1513617735');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (12, 'admin', '2023-06-19 00:00:00', '测试评论-797846303');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (8, 'admin', '2023-02-03 00:00:00', '测试评论-316104427');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (8, 'admin', '2023-10-14 00:00:00', '测试评论-739014682');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'admin', '2023-03-18 00:00:00', '测试评论1684079350');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'admin', '2022-12-08 00:00:00', '测试评论-191954674');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (9, 'admin', '2023-11-04 00:00:00', '测试评论2004188408');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (4, 'admin', '2023-03-10 00:00:00', '测试评论196010549');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (4, 'admin', '2023-05-18 00:00:00', '测试评论-423328936');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (1, 'admin', '2023-04-23 00:00:00', '测试评论-46743787');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'admin', '2023-04-15 00:00:00', '测试评论-1926067144');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (9, 'admin', '2022-12-04 00:00:00', '测试评论1212928547');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (1, 'admin', '2023-02-12 00:00:00', '测试评论2030862991');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (12, 'admin', '2023-07-13 00:00:00', '测试评论-1070355816');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'admin', '2023-01-23 00:00:00', '测试评论-911608736');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (6, 'admin', '2023-09-25 00:00:00', '测试评论-1389942835');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (8, 'admin', '2023-11-02 00:00:00', '测试评论-1287163858');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'admin', '2023-07-31 00:00:00', '测试评论-2079762997');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (2, 'admin', '2023-11-03 00:00:00', '测试评论-987445842');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (4, 'admin', '2023-07-27 00:00:00', '测试评论-1428750558');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'admin', '2023-09-19 00:00:00', '测试评论-1770381890');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (12, 'admin', '2023-01-09 00:00:00', '测试评论-2019751943');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (9, 'admin', '2023-11-08 00:00:00', '测试评论-1064165994');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (7, 'admin', '2023-04-06 00:00:00', '测试评论-569317786');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'admin', '2023-11-23 00:00:00', '测试评论1720092988');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'admin', '2023-09-01 00:00:00', '测试评论1439157248');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (9, 'admin', '2023-10-17 00:00:00', '测试评论285219133');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'admin', '2023-04-12 00:00:00', '测试评论-843164495');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'admin', '2023-04-13 00:00:00', '测试评论-583960679');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (2, 'admin', '2023-06-02 00:00:00', '测试评论-626274608');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'admin', '2023-07-10 00:00:00', '测试评论-1521444866');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (7, 'admin', '2023-05-17 00:00:00', '测试评论-1044811666');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (12, 'admin', '2023-01-13 00:00:00', '测试评论223403833');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'admin', '2023-11-18 00:00:00', '测试评论1374096879');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'admin', '2023-05-19 00:00:00', '测试评论601083369');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'admin', '2023-01-18 00:00:00', '测试评论-521394724');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (8, 'admin', '2023-10-09 00:00:00', '测试评论1684873845');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (2, 'admin', '2023-06-07 00:00:00', '测试评论771577336');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (9, 'admin', '2023-07-14 00:00:00', '测试评论1756538431');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'admin', '2023-02-21 00:00:00', '测试评论583765392');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (4, 'admin', '2023-04-04 00:00:00', '测试评论-127985127');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'admin', '2023-01-23 00:00:00', '测试评论177443095');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'admin', '2023-10-21 00:00:00', '测试评论-1588398601');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'admin', '2023-06-22 00:00:00', '测试评论1701421809');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'admin', '2023-02-02 00:00:00', '测试评论-1246433306');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'admin', '2023-06-08 00:00:00', '测试评论-1806277233');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'admin', '2023-08-27 00:00:00', '测试评论-601915843');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (8, 'admin', '2023-06-22 00:00:00', '测试评论647449275');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (10, 'admin', '2022-12-31 00:00:00', '测试评论317180177');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'admin', '2023-04-16 00:00:00', '测试评论312878132');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (7, 'admin', '2023-07-04 00:00:00', '测试评论-1487028499');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (1, 'admin', '2023-03-08 00:00:00', '测试评论-204428931');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (8, 'admin', '2023-04-04 00:00:00', '测试评论-1548992318');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (10, 'admin', '2023-05-19 00:00:00', '测试评论-396228879');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (7, 'admin', '2023-01-07 00:00:00', '测试评论1157339420');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'admin', '2023-05-18 00:00:00', '测试评论-1578251749');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'admin', '2023-10-04 00:00:00', '测试评论1189589654');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (7, 'admin', '2023-10-05 00:00:00', '测试评论-227754172');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (4, 'admin', '2023-04-16 00:00:00', '测试评论209633680');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'admin', '2023-07-02 00:00:00', '测试评论-1026452404');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'admin', '2023-10-20 00:00:00', '测试评论-1324067554');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (2, 'admin', '2023-07-18 00:00:00', '测试评论600069309');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (7, 'admin', '2023-03-16 00:00:00', '测试评论-813694141');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'admin', '2023-03-17 00:00:00', '测试评论-1668213988');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (10, 'admin', '2023-08-14 00:00:00', '测试评论-1004657570');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'admin', '2023-04-25 00:00:00', '测试评论541545347');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'test', '2023-10-21 00:00:00', '测试评论-1278987535');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (8, 'test', '2023-06-22 00:00:00', '测试评论-2082622192');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (8, 'test', '2023-02-02 00:00:00', '测试评论431457578');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (2, 'test', '2023-06-08 00:00:00', '测试评论-454834539');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (4, 'test', '2023-08-27 00:00:00', '测试评论-1181714959');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (6, 'test', '2023-06-22 00:00:00', '测试评论-1314377889');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'test', '2023-07-13 00:00:00', '测试评论-1477614622');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (4, 'test', '2023-01-23 00:00:00', '测试评论-1694155389');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (7, 'test', '2023-09-25 00:00:00', '测试评论-108752644');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (7, 'test', '2023-11-02 00:00:00', '测试评论708545771');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (9, 'test', '2023-07-31 00:00:00', '测试评论1988405724');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (10, 'test', '2023-03-14 00:00:00', '测试评论1548846001');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'test', '2023-06-19 00:00:00', '测试评论-1088811987');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (7, 'test', '2023-07-10 00:00:00', '测试评论-855785553');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (10, 'test', '2022-12-22 00:00:00', '测试评论895320158');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (10, 'test', '2022-12-21 00:00:00', '测试评论810699094');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'test', '2022-12-26 00:00:00', '测试评论-1274520938');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (8, 'test', '2023-05-18 00:00:00', '测试评论929537982');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'test', '2023-04-22 00:00:00', '测试评论-1634388252');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'test', '2023-10-07 00:00:00', '测试评论-1734086145');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (10, 'test', '2023-04-03 00:00:00', '测试评论-1640774469');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (9, 'test', '2023-03-04 00:00:00', '测试评论-248768066');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'test', '2023-10-09 00:00:00', '测试评论-1972808292');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (4, 'test', '2023-05-27 00:00:00', '测试评论-2016335094');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (10, 'test', '2023-01-11 00:00:00', '测试评论-1549851883');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (9, 'test', '2023-08-12 00:00:00', '测试评论-1415139992');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (1, 'test', '2023-04-10 00:00:00', '测试评论-1267040279');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (6, 'test', '2023-05-19 00:00:00', '测试评论-694496724');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'test', '2023-01-18 00:00:00', '测试评论-1544918896');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (2, 'test', '2023-10-09 00:00:00', '测试评论-668255222');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'test', '2023-06-07 00:00:00', '测试评论-258398495');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (9, 'test', '2023-07-14 00:00:00', '测试评论-622449684');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (11, 'test', '2023-02-21 00:00:00', '测试评论-567804659');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (9, 'test', '2023-07-02 00:00:00', '测试评论-233756923');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (2, 'test', '2023-10-20 00:00:00', '测试评论1753615311');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (10, 'test', '2023-07-18 00:00:00', '测试评论818637349');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'test', '2023-03-16 00:00:00', '测试评论1762846689');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (1, 'test', '2023-03-17 00:00:00', '测试评论-627851698');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (8, 'test', '2023-08-14 00:00:00', '测试评论2135986865');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (10, 'test', '2023-04-25 00:00:00', '测试评论-1686207864');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'test', '2023-06-08 00:00:00', '测试评论-84948396');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'test', '2023-08-27 00:00:00', '测试评论-1914568975');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'test', '2023-06-22 00:00:00', '测试评论574416058');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (3, 'test', '2023-07-13 00:00:00', '测试评论554456343');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (2, 'test', '2023-01-23 00:00:00', '测试评论497199651');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (2, 'test', '2023-06-08 00:00:00', '测试评论1240965514');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (5, 'test', '2023-08-27 00:00:00', '测试评论-1571036462');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (8, 'test', '2023-06-22 00:00:00', '测试评论690365099');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (6, 'test', '2022-12-31 00:00:00', '测试评论851676159');
INSERT INTO starfall.comment (topicid, user, date, content) VALUES (1, 'test', '2023-04-16 00:00:00', '测试评论-115771190');
