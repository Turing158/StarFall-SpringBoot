package com.starfall;

import com.starfall.service.UserService;
import com.starfall.util.AECSecure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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


    @Test
    public void testLogin(){
        System.out.println(userService.login("admin1", ""));
    }
}
