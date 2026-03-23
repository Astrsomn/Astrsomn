package org.astrsomn.starter.langchain.tool.local;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import dev.langchain4j.service.tool.ToolExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class LocalToolCacheManager {


    // 缓存已经解析好的工具元数据和执行器
    // Key: beanName + ":" + methodName
    private final Map<String, ToolDefinition> cache = new ConcurrentHashMap<>();

    @Data
    @AllArgsConstructor
    public static class ToolDefinition {
        private final ToolSpecification specification;
        private final ToolExecutor executor;
    }

    public ToolDefinition getOrCompute(Object bean, String beanName, String methodName) {
        String key = beanName + ":" + methodName;
        return cache.computeIfAbsent(key, k -> {
            log.info("首次加载工具方法: {}", key);
            Method method = Arrays.stream(bean.getClass().getMethods())
                    .filter(m -> m.getName().equals(methodName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Method not found: " + methodName));

            // 一次性生成 Specification 和 Executor，这是最耗时的步骤
            ToolSpecification spec = ToolSpecifications.toolSpecificationFrom(method);
            ToolExecutor executor = new DefaultToolExecutor(bean, method);

            return new ToolDefinition(spec, executor);
        });
    }


}
