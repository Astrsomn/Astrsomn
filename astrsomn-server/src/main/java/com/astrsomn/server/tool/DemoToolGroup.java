package com.astrsomn.server.tool;

import dev.langchain4j.agent.tool.Tool;
import com.astrsomn.starter.langchain.aop.annotation.AstroToolGroup;
import org.springframework.stereotype.Component;

@AstroToolGroup(value = "demo", description = "演示工具组")
@Component
public class DemoToolGroup {

    @Tool("获取当前时间")
    public String getCurrentTime() {
        return "当前时间: " + System.currentTimeMillis();
    }

    @Tool(value = {"计算两个数的和"})
    public int add(int a, int b) {
        return a + b;
    }

    @Tool(name = "greet", value = {"向用户打招呼"})
    public String greet(String name) {
        return "你好, " + name + "!";
    }

    @Tool("获取随机数")
    public int getRandomNumber(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }
}
