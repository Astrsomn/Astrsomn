package com.astrsomn.starter.langchain.tool.local;


import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.core.common.entity.AiToolEntity;
import org.springframework.context.ApplicationContext;

import java.util.List;

@Slf4j
public class DynamicToolProvider implements ToolProvider {

    private final List<AiToolEntity> toolConfigs;
    private final ApplicationContext applicationContext;
    private final LocalToolCacheManager globalToolCache; // 注入全局缓存

    public DynamicToolProvider(List<AiToolEntity> toolConfigs,
                               ApplicationContext applicationContext,
                               LocalToolCacheManager globalToolCache) {
        this.toolConfigs = toolConfigs;
        this.applicationContext = applicationContext;
        this.globalToolCache = globalToolCache;
    }

    @Override
    public ToolProviderResult provideTools(ToolProviderRequest request) {
        ToolProviderResult.Builder builder = ToolProviderResult.builder();

        for (AiToolEntity config : toolConfigs) {
            try {
                Object bean = applicationContext.getBean(config.getBeanName());

                // 从全局缓存获取（如果没缓存过则内部会触发反射解析）
                LocalToolCacheManager.ToolDefinition definition =
                        globalToolCache.getOrCompute(bean, config.getBeanName(), config.getMethodName());

                ToolSpecification spec = definition.getSpecification();

                // 如果数据库有自定义描述，按需覆盖（spec 是不可变的，需 rebuild）
                if (config.getDescription() != null) {
                    spec = spec.toBuilder().description(config.getDescription()).build();
                }

                builder.add(spec, definition.getExecutor());

            } catch (Exception e) {
                log.error("组装工具 [{}] 失败", config.getToolName(), e);
            }
        }
        return builder.build();
    }
}
