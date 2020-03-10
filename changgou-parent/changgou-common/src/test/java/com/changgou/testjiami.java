package com.changgou;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class testjiami {

    @Autowired
    StringEncryptor stringEncryptor;
    @Test
    public void fun01(){

        String result = stringEncryptor.encrypt("12345678");
        System.out.println(result);

        String decrypt = stringEncryptor.decrypt("0mGDFku9Tay5qo2qOLQs1pEGl+Vhv1wH");
        System.out.println(decrypt);

    }
}
