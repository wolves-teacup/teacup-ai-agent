package com.teacup.teacupaiagent.controller;

import com.teacup.teacupaiagent.agent.TeaCupManus;
import com.teacup.teacupaiagent.app.WorkApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private WorkApp workApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 同步调用 AI 应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/work_app/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId) {
        return workApp.doChat(message, chatId);
    }


/**
 * 使用Server-Sent Events (SSE)方式实现与工作应用的聊天功能
 * 该接口返回一个Flux<String>类型的响应流，用于实时返回聊天内容
 *
 * @param message 用户发送的消息内容
 * @param chatId 聊天会话的唯一标识符
 * @return 返回一个Flux<String>类型的响应流，用于实时推送聊天回复
 */
    @GetMapping(value = "/work_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)  // 指定使用SSE内容类型
    public Flux<String> doChatWithWorkAppSSE(String message, String chatId) {  // 定义聊天接口方法
        return workApp.doChatByStream(message, chatId);  // 调用工作应用的流式聊天方法并返回结果流
    }




/**
 * 处理与应用的聊天请求，使用SSE(Server-Sent Events)技术实现服务器到客户端的实时推送
 * @param message 用户发送的消息内容
 * @param chatId 聊天会话的唯一标识符
 * @return 返回SseEmitter对象，用于建立长连接并推送响应数据
 */
    @GetMapping(value = "/work_app/chat/sse_emitter")
    public SseEmitter doChatWithWorkAppServerSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter，设置为3分钟(180000毫秒)
        SseEmitter sseEmitter = new SseEmitter(180000L); // 3 分钟超时
        // 获取 Flux 响应式数据流并且直接通过订阅推送给 SseEmitter
    // 使用响应式编程的方式处理聊天消息流
        workApp.doChatByStream(message, chatId)
            // 订阅数据流，处理每个数据块(chunk)
                .subscribe(chunk -> {
                    try {
                    // 将处理后的数据块发送给客户端
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                    // 如果发生IO异常，完成Emitter并传递错误
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        // 返回
        return sseEmitter;
    }

    /**
     * 流式调用 Manus 超级智能体
     *
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        TeaCupManus teaCupManus = new TeaCupManus(allTools, dashscopeChatModel);
        return teaCupManus.runStream(message);
    }
}