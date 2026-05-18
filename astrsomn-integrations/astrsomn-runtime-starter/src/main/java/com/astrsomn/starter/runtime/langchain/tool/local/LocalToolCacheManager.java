package com.astrsomn.starter.runtime.langchain.tool.local;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import dev.langchain4j.service.tool.ToolExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class LocalToolCacheManager {

    private final Map<String, ToolDefinition> cache = new ConcurrentHashMap<>();

    public ToolDefinition getOrCompute(Object bean, String beanName, String methodName) {
        String key = beanName + ":" + methodName;
        return cache.computeIfAbsent(key, k -> {
            log.info("首次加载工具方法: {}", key);
            Method method = Arrays.stream(bean.getClass().getMethods())
                    .filter(m -> m.getName().equals(methodName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Method not found: " + methodName));
            ToolSpecification spec = ToolSpecifications.toolSpecificationFrom(method);
            ToolExecutor executor = new DefaultToolExecutor(bean, method);

            return new ToolDefinition(spec, executor);
        });
    }

    @AllArgsConstructor
        public record ToolDefinition(ToolSpecification specification, ToolExecutor executor) {
    }


}
