package com.teacup.teacupaiagent.agent.model;

/**
 * AgentState枚举类，表示代理(Agent)的不同状态
 * 该枚举定义了代理可能处于的所有状态
 */
public enum AgentState {
  
      
    // 空闲状态，表示代理当前未执行任何任务
    IDLE,
  
      
    // 运行状态，表示代理正在执行任务
    RUNNING,
  
      
    // 完成状态，表示代理已成功完成其任务
    FINISHED,
  
      
    // 错误状态，表示代理在执行任务过程中遇到了错误
    ERROR
}
