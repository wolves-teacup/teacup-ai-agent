package com.teacup.teacupaiagent.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ReActAgent类是一个抽象类，继承自BaseAgent类。
 * 使用了Lombok的@Data和@EqualsAndHashCode注解，简化了getter、setter和equals/hashCode方法的编写。
 * @EqualsAndHashCode(callSuper = true)确保在生成equals和hashCode方法时考虑父类的字段。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ReActAgent extends BaseAgent {  
  
      
    /**
     * 抽象方法：思考方法
     * @return 返回一个布尔值，表示是否需要执行行动
     */
    public abstract boolean think();
  
      
    /**
     * 抽象方法：行动方法
     * @return 返回一个字符串，表示行动的结果
     */
    public abstract String act();
  
      
    /**
     * 重写父类的step方法，执行思考-行动的循环
     * @return 返回执行结果或错误信息
     */
    @Override
    public String step() {  
        try {  
            // 调用think方法进行思考
            boolean shouldAct = think();
            // 如果思考结果为不需要行动，返回特定信息
            if (!shouldAct) {
                return "思考完成 - 无需行动";  
            }  
            // 否则调用act方法执行行动
            return act();
        } catch (Exception e) {  
            
            // 捕获并打印异常信息
            e.printStackTrace();
            // 返回错误信息
            return "步骤执行失败: " + e.getMessage();
        }  
    }  
}