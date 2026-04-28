package com.teacup.teacupaiagent.demo;

import com.teacup.teacupaiagent.app.WorkApp;
import com.teacup.teacupaiagent.tools.PDFGenerationTool;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class PDFGenerationToolTest {

    @Test
    public void testGeneratePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "茶杯云图.pdf";
        String content = "茶杯云图 https://www.codefather.cn";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }


    @Resource
    private WorkApp workApp;
    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();

        String message = "我的居住在西安长安区，请帮我找到 5 公里内合适的拍摄风景图的地点";
        String answer =  workApp.doChatWithMcp(message, chatId);
    }
}