package org.astrsomn.starter.langchain.tool.mcp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.entity.AiMcpEntity;
import org.astrsomn.core.mapper.AiMcpMapper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DynamicMcpToolProvider implements ToolProvider {

    private final McpManager mcpManager;
    private final List<AiMcpEntity> mcpConfigs;

    public DynamicMcpToolProvider(List<AiMcpEntity> mcpConfigs, McpManager mcpManager) {
        this.mcpManager = mcpManager;
        this.mcpConfigs = mcpConfigs;
    }

    @Override
    public ToolProviderResult provideTools(ToolProviderRequest request) {
        ToolProviderResult.Builder builder = ToolProviderResult.builder();
        for (AiMcpEntity config : mcpConfigs) {
            McpClient client = mcpManager.getOrCreateClient(config);
            List<ToolSpecification> specs = mcpManager.getCachedTools(config.getId());

            // 为该 Client 的所有工具复用同一个转发执行器
            ToolExecutor executor = (execReq, memoryId) -> client.executeTool(execReq);
            specs.forEach(spec -> builder.add(spec, executor));
        }
        return builder.build();
    }
}