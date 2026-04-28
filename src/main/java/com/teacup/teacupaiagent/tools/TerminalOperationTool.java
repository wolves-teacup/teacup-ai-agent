package com.teacup.teacupaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 终端操作工具类，用于执行终端命令并获取输出结果
 */
public class TerminalOperationTool {

    /**
     * 在终端中执行命令并返回输出结果
     * @param command 要在终端中执行的命令
     * @return 命令执行后的输出结果，包括标准输出和错误信息
     */
    @Tool(description = "Execute a command in the terminal")
    public String executeTerminalCommand(@ToolParam(description = "Command to execute in the terminal") String command) {
        // 用于存储命令输出的字符串构建器
        StringBuilder output = new StringBuilder();
        try {
            // 创建进程构建器，使用Windows命令行(cmd.exe)执行命令
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);

            // 启动进程
            Process process = builder.start();
            // 使用try-with-resources语句确保BufferedReader被正确关闭
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                // 逐行读取进程的输出
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            // 等待进程执行完成并获取退出码
            int exitCode = process.waitFor();
            // 如果退出码不为0，表示命令执行失败
            if (exitCode != 0) {
                output.append("Command execution failed with exit code: ").append(exitCode);
            }
        } catch (IOException | InterruptedException e) {
            // 捕获并处理可能的IO异常或中断异常
            output.append("Error executing command: ").append(e.getMessage());
        }
        return output.toString();
    }
}