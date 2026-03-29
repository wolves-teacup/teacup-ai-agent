package com.teacup.teacupaiagent.app;


import com.teacup.teacupaiagent.advisor.MyLoggerAdvisor;
import com.teacup.teacupaiagent.chatmemory.FileBasedChatMemory;
import com.teacup.teacupaiagent.rag.WorkAppRagCustomAdvisorFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class WorkApp {

    private final ChatClient chatClient;

    private static final Logger log = LoggerFactory.getLogger(MyLoggerAdvisor.class);


    private static final String SYSTEM_PROMPT = "你是图片工坊 AI 助手，专业解答图片上传、空间管理、权限控制、协同编辑、图片检索等功能问题。回答要简洁清晰、结构化，主动提供操作步骤和最佳实践。遇到模糊问题要追问，必要时推荐学习资源（https://www.baidu.com）。严禁编造功能或泄露敏感信息，保持友好专业的服务态度。";

    /**
     *
     * @param dashscopeChatModel
     */
    public WorkApp(ChatModel dashscopeChatModel){

        //
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);

        //初始化基于内存的对话记忆
//        ChatMemory chatMemory = new InMemoryChatMemory();

        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new MyLoggerAdvisor()
                )
                .build();

    }


    /**
     * AI基础对话（支持多轮对话记忆）
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


    record WorkReport(String title, List<String> suggestions) {
    }


    public WorkReport doChatWithReport(String message, String chatId) {

        //.system()设置系统提示词
        //.user()设置用户提示词
        //.call()执行调用，将提示词和记忆上下文发送给大模型

        WorkReport workReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成工作结果，标题为{用户名}的工作报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(WorkReport.class);
        log.info("workReport: {}", workReport);
        return workReport;

    }

    //  知识库问答功能

    @Resource
    private VectorStore workAppVectorStore;

    @Resource
    private Advisor workAppRagCloudAdvisor;


    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))

//                .advisors(new QuestionAnswerAdvisor(workAppVectorStore))
                //应用Rag检索增强服务（云知识库）
                .advisors(workAppRagCloudAdvisor)
                .advisors(WorkAppRagCustomAdvisorFactory.createWorkAppRagCustomAdvisor(workAppVectorStore, "QA"))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


    @Resource
    private ToolCallback[] allTools;

    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))

                .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
}
