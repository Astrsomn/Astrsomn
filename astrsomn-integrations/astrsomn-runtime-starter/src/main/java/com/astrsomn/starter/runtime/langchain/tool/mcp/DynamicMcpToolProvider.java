package com.astrsomn.starter.runtime.langchain.tool.mcp;

import com.astrsomn.api.runtime.common.entity.AiMcpEntity;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class DynamicMcpToolProvider implements ToolProvider {

    private final McpToolCacheManager mcpToolManager;
    private final List<AiMcpEntity> mcpConfigs;

    @Override
    public ToolProviderResult provideTools(ToolProviderRequest request) {
        ToolProviderResult.Builder builder = ToolProviderResult.builder();
        for (AiMcpEntity config : mcpConfigs) {
            McpClient client = mcpToolManager.getOrCreateClient(config);
            List<ToolSpecification> specs = mcpToolManager.getCachedTools(config.getId());
            ToolExecutor executor = (execReq, memoryId) -> client.executeTool(execReq);
            specs.forEach(spec -> builder.add(spec, executor));
        }
        return builder.build();
    }
}