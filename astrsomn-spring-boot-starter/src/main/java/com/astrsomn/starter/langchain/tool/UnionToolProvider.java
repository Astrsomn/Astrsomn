package com.astrsomn.starter.langchain.tool;

import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;
import lombok.extern.slf4j.Slf4j;

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
                ToolProviderResult result = provider.provideTools(request);
                if (result != null && result.tools() != null) {
                    result.tools().forEach(resultBuilder::add);
                }
            } catch (Exception e) {
                log.error("Provider {} 加载工具失败", provider.getClass().getSimpleName(), e);
            }
        }

        return resultBuilder.build();
    }
}
