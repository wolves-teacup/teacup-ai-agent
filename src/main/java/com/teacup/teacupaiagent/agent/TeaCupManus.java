package com.teacup.teacupaiagent.agent;

import com.teacup.teacupaiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * TeaCupManus 类继承自 ToolCallAgent，是一个功能强大的AI助手组件。
 * 该类通过配置各种工具和提示信息，能够解决用户提出的各种任务。
 */
@Component
public class TeaCupManus extends ToolCallAgent {

    /**
     * TeaCupManus 的构造函数，用于初始化AI助手实例。
     * @param allTools 可用的工具回调数组，用于执行各种任务
     * @param dashscopeChatModel DashScope聊天模型，用于AI对话
     */
    public TeaCupManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);
        this.setName("TeaCupManus");
        // 设置系统提示信息，定义AI助手的身份和能力
        String SYSTEM_PROMPT = """
                You are TeaCupManus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        // 设置下一步操作提示信息，指导AI如何使用工具完成任务
        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the `terminate` tool/function call.
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        // 设置最大执行步数，防止无限循环
        this.setMaxSteps(20);
        // 初始化 AI 对话客户端，配置日志记录器
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);
    }
}