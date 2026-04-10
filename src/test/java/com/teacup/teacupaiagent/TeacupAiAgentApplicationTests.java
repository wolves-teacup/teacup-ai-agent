package com.teacup.teacupaiagent;

import com.teacup.teacupaiagent.app.WorkApp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest(properties = "spring.profiles.active=test")
class TeacupAiAgentApplicationTests {

    @Resource
    private WorkApp workApp;

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "76号混凝土拌意大利面好吃吗？";
        String answer = workApp.doChatWithRag(message,chatId);
        Assertions.assertNotNull(answer);
    }


    @Test
    void doChatWithTools() {

        //网络搜索功能测试WebSearchTool
        testMessage("周末想带拍一些风景图，有什么地方推荐？");

        //网页抓取功能测试WebScrapingTool
        testMessage("最近和对象吵架了，看看百度（baidu.com）的其他情侣是怎么解决矛盾的？");

        //资源下载功能测试ResourceDownloadTool
        testMessage("直接下载一张适合做手机壁纸的星空图片为文件");

        //终端命令执行功能测试TerminalOperationTool
        testMessage("执行 Python3 脚本来生成数据分析报告");

        //文件操作功能测试FileOperationTool
        testMessage("保存我的日常生活档案为文件");

        //PDF生成功能测试PDFGenerationTool
        testMessage("我是该云图库项目的摄影师，生成一份‘拍摄日志记录’PDF，包含拍摄风景图的地点");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = workApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }
}
