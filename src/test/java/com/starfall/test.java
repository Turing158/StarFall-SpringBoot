package com.starfall;

import com.starfall.dao.TopicDao;
import com.starfall.dao.UserDao;
import com.starfall.entity.User;
import com.starfall.service.UserService;
import com.starfall.util.AECSecure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@SpringBootTest
@RunWith(SpringRunner.class)
public class test {

    @Test
    public void testEncrypt(){
        AECSecure aec = new AECSecure();
        String str = "admin";
        String encrypt = aec.encrypt(str);
        System.out.println(encrypt);
    }
    @Autowired
    UserService userService;


    @Autowired
    UserDao userDao;
    @Autowired
    AECSecure aecSecure;

    @Test
    public void testLogin(){
//        System.out.println(userService.login("admin1", "",""));
//        String password = userDao.findByUserOrEmail("admin").getPassword();
//        System.out.println(aecSecure.decrypt(password));
        User user = new User();
        user.setUser("StarFall");
        user.setPassword(aecSecure.encrypt("admin"));
        userDao.insertUser(user);
    }

    @Autowired
    TopicDao topicDao;
    @Test
    public void testTopic(){
        System.out.println(topicDao.findTopicInfoById(1));
    }


    @Test
    public void insert100comment(){
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            System.out.println(topicDao.insertComment(r.nextInt(1,12), "admin", null, "测试评论"+r.nextInt()));
        }
    }

    @Test
    public void testUpdateUser(){
        System.out.println(userDao.updatePassword("admin", aecSecure.encrypt("admin")));
    }

    @Test
    public void testGetComment(){
        System.out.println(topicDao.findCommentByTopicId(1,0));
    }
}
