package org.astrsomn.starter.langchain.tool;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UnionToolProvider implements ToolProvider {

    private final List<ToolProvider> providers;

    public UnionToolProvider(List<ToolProvider> providers) {
        this.providers = providers;
    }

    @Override
    public ToolProviderResult provideTools(ToolProviderRequest request) {
        ToolProviderResult.Builder resultBuilder = ToolProviderResult.builder();

        if (providers == null || providers.isEmpty()) {
            return resultBuilder.build();
        }

        for (ToolProvider provider : providers) {
            try {
                // 获取单个 Provider 提供的工具结果
                ToolProviderResult result = provider.provideTools(request);

                if (result != null && result.tools() != null) {
                    // 将该 Provider 中的所有工具规格（Specification）和执行器（Executor）合并
                    // result.tools() 返回的是 Map<ToolSpecification, ToolExecutor>
                    result.tools().forEach(resultBuilder::add);
                }
            } catch (Exception e) {
                // 记录错误，但不中断其他 Provider 的加载
                log.error("Provider {} 加载工具失败", provider.getClass().getSimpleName(), e);
            }
        }

        return resultBuilder.build();
    }
}
