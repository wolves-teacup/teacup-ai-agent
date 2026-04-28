package com.teacup.teacupaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * spring ai 框架调用ai大模型
 */
@Component
public class SpringAiAiInvoke implements CommandLineRunner {


    @Resource
    private ChatModel dashscopeChatModel;

    @Override
    public void run(String... args) throws Exception {

//        AssistantMessage output = dashscopeChatModel.call(new Prompt("我是茶杯"))
//                .getResult()
//                .getOutput();
//
//        System.out.println(output.getText());

    }
}
