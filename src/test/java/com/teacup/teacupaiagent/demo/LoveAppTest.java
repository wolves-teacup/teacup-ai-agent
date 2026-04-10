package com.teacup.teacupaiagent.demo;

import com.teacup.teacupaiagent.app.WorkApp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest(properties = "spring.profiles.active=test")
class LoveAppTest {

    @Resource
    private WorkApp loveApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        
        String message = "你好，我是程序员鱼皮";
        String answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

    }


    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();

        String message = "我的居住在西安长安区，请帮我找到 5 公里内合适的拍摄风景图的地点";
        String answer =  loveApp.doChatWithMcp(message, chatId);
    }
}