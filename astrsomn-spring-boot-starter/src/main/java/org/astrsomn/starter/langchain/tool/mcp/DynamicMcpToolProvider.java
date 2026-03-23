package org.astrsomn.starter.langchain.tool.mcp;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.entity.AiMcpEntity;

import java.util.List;

@Slf4j
public class DynamicMcpToolProvider implements ToolProvider {

    private final McpToolCacheManager mcpToolManager;
    private final List<AiMcpEntity> mcpConfigs;

    public DynamicMcpToolProvider(List<AiMcpEntity> mcpConfigs, McpToolCacheManager mcpToolManager) {
        this.mcpToolManager = mcpToolManager;
        this.mcpConfigs = mcpConfigs;
    }

    @Override
    public ToolProviderResult provideTools(ToolProviderRequest request) {
        ToolProviderResult.Builder builder = ToolProviderResult.builder();
        for (AiMcpEntity config : mcpConfigs) {
            McpClient client = mcpToolManager.getOrCreateClient(config);
            List<ToolSpecification> specs = mcpToolManager.getCachedTools(config.getId());

            // 为该 Client 的所有工具复用同一个转发执行器
            ToolExecutor executor = (execReq, memoryId) -> client.executeTool(execReq);
            specs.forEach(spec -> builder.add(spec, executor));
        }
        return builder.build();
    }
}