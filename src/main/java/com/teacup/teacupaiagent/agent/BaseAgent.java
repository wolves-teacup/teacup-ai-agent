package com.teacup.teacupaiagent.agent;



import cn.hutool.core.util.StrUtil;
import com.teacup.teacupaiagent.advisor.MyLoggerAdvisor;
import com.teacup.teacupaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * BaseAgent 是一个抽象基类，为所有智能代理提供基础功能。
 * 使用了 Lombok 的 @Data 和 @Slf4j 注解，分别用于自动生成getter/setter方法和日志记录器。
 */
@Data
@Slf4j
public abstract class BaseAgent {  
  
    
    /**
     * 代理的名称
     */
    private String name;
  
    
    /**
     * 系统提示信息，用于指导代理的行为
     */
    private String systemPrompt;
    /**
     * 下一步提示信息，用于指导代理的下一步操作
     */
    private String nextStepPrompt;
  
    
    /**
     * 代理的当前状态，默认为IDLE（空闲）
     */
    private AgentState state = AgentState.IDLE;
  
    
    /**
     * 代理执行的最大步数
     */
    private int maxSteps = 10;
    /**
     * 当前已执行的步数
     */
    private int currentStep = 0;
  
    
    /**
     * 聊天客户端，用于与用户进行交互
     */
    private ChatClient chatClient;
  
    
    /**
     * 消息列表，存储代理与用户之间的所有消息
     */
    private List<Message> messageList = new ArrayList<>();


    protected static final Logger log = LoggerFactory.getLogger(MyLoggerAdvisor.class);
  
      
    /**
     * 运行代理，执行用户提示
     * @param userPrompt 用户输入的提示信息
     * @return 执行结果
     */
    public String run(String userPrompt) {
        // 检查代理状态是否为空闲
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent from state: " + this.state);  
        }  
        // 检查用户提示是否为空
        if (StrUtil.isBlank(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty user prompt");  
        }  
        
        // 设置代理状态为运行中
        this.state = AgentState.RUNNING;
        
        // 添加用户消息到消息列表
        messageList.add(new UserMessage(userPrompt));
        
        // 存储每一步的执行结果
        List<String> results = new ArrayList<>();
        try {  
            // 循环执行步骤，直到达到最大步数或代理状态为FINISHED
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;  
                currentStep = stepNumber;  
                log.info("Executing step " + stepNumber + "/" + maxSteps);  
                
                // 执行一步并记录结果
                String stepResult = step();
                String result = "Step " + stepNumber + ": " + stepResult;  
                results.add(result);  
            }  
            
            // 检查是否达到最大步数
            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;  
                results.add("Terminated: Reached max steps (" + maxSteps + ")");  
            }  
            // 返回所有步骤的执行结果
            return String.join("\n", results);
        } catch (Exception e) {  
            // 捕获异常并设置代理状态为ERROR
            state = AgentState.ERROR;
            log.error("Error executing agent", e);  
            return "执行错误" + e.getMessage();  
        } finally {  
            
            // 无论是否发生异常，都执行清理工作
            this.cleanup();
        }  
    }  
  
      
    /**
     * 抽象方法，由子类实现具体的步骤逻辑
     * @return 步骤执行的结果
     */
    public abstract String step();
  
      
    /**
     * 清理方法，用于在代理执行完成后进行资源清理
     * 子类可以重写此方法来实现特定的清理逻辑
     */
    protected void cleanup() {
        
    }

    //nextStepPrompt的get和set方法
    public String getNextStepPrompt() {
        return nextStepPrompt;
    }

    public void setNextStepPrompt(String nextStepPrompt) {
        this.nextStepPrompt = nextStepPrompt;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public ChatClient getChatClient() {
        return chatClient;
    }

    public void setChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AgentState getState() {
        return state;
    }

    public void setState(AgentState state) {
        this.state = state;
    }


    public int getMaxSteps() {
        return maxSteps;
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }



    public SseEmitter runStream(String userPrompt) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter sseEmitter = new SseEmitter(300000L); // 5 分钟超时
        // 使用线程异步处理，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            // 1、基础校验
            try {
                if (this.state != AgentState.IDLE) {
                    sseEmitter.send("错误：无法从状态运行代理：" + this.state);
                    sseEmitter.complete();
                    return;
                }
                if (StrUtil.isBlank(userPrompt)) {
                    sseEmitter.send("错误：不能使用空提示词运行代理");
                    sseEmitter.complete();
                    return;
                }
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }
            // 2、执行，更改状态
            this.state = AgentState.RUNNING;
            // 记录消息上下文
            messageList.add(new UserMessage(userPrompt));
            // 保存结果列表
            List<String> results = new ArrayList<>();
            try {
                // 执行循环
                for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                    int stepNumber = i + 1;
                    currentStep = stepNumber;
                    log.info("Executing step {}/{}", stepNumber, maxSteps);
                    // 单步执行
                    String stepResult = step();
                    String result = "Step " + stepNumber + ": " + stepResult;
                    results.add(result);
                    // 输出当前每一步的结果到 SSE
                    sseEmitter.send(result);
                }
                // 检查是否超出步骤限制
                if (currentStep >= maxSteps) {
                    state = AgentState.FINISHED;
                    results.add("Terminated: Reached max steps (" + maxSteps + ")");
                    sseEmitter.send("执行结束：达到最大步骤（" + maxSteps + "）");
                }
                // 正常完成
                sseEmitter.complete();
            } catch (Exception e) {
                state = AgentState.ERROR;
                log.error("error executing agent", e);
                try {
                    sseEmitter.send("执行错误：" + e.getMessage());
                    sseEmitter.complete();
                } catch (IOException ex) {
                    sseEmitter.completeWithError(ex);
                }
            } finally {
                // 3、清理资源
                this.cleanup();
            }
        });

        // 设置超时回调
        sseEmitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE connection timeout");
        });
        // 设置完成回调
        sseEmitter.onCompletion(() -> {
            if (this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("SSE connection completed");
        });
        return sseEmitter;
    }


}
