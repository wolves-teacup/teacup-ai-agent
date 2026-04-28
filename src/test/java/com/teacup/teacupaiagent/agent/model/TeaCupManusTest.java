package com.teacup.teacupaiagent.agent.model;

import com.teacup.teacupaiagent.agent.TeaCupManus;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TeaCupManusTest {

    @Resource
    private TeaCupManus teaCupManus;

    @Test
    public void run() {
        String userPrompt= """
                我住在西安市长安区，请帮我找到5公里内合适的拍摄风景照的地点，
                制定一份详细的拍摄计划，并以PDF格式输出
                """;

        String answer= teaCupManus.run(userPrompt);
        Assertions.assertAll(answer);
    }
}