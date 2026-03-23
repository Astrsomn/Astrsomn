package org.astrsomn.starter.langchain.mcp;

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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DynamicMcpToolProvider implements ToolProvider {

    private final McpManager mcpManager;
    private final AiMcpMapper configMapper;

    private List<AiMcpEntity> mcpConfigList = new ArrayList<>();
    private List<McpClient> mcpClients;

    public DynamicMcpToolProvider(McpManager mcpManager, AiMcpMapper configMapper) {
        this.mcpManager = mcpManager;
        this.configMapper = configMapper;
    }

    public void initialize(List<String> mcpIds) {
        this.mcpConfigList = configMapper.selectList(new LambdaQueryWrapper<AiMcpEntity>()
                .in(AiMcpEntity::getId, mcpIds));

        mcpClients = mcpConfigList.stream()
                .map(mcpManager::getOrCreateClient)
                .collect(Collectors.toList());
    }

    @Override
    public ToolProviderResult provideTools(ToolProviderRequest request) {
        ToolProviderResult.Builder resultBuilder = ToolProviderResult.builder();

        if (mcpClients != null) {
            for (McpClient client : mcpClients) {
                try {
                    // 1. 获取该 MCP Server 定义的所有工具规格
                    List<ToolSpecification> toolSpecifications = client.listTools();

                    for (ToolSpecification specification : toolSpecifications) {
                        // 2. 为每个规格创建一个执行器
                        // 当大模型决定调用这个工具时，这个 lambda 会被触发
                        ToolExecutor executor = (toolExecutionRequest, memoryId) -> {
                            // 转发给 McpClient 去远程执行
                            return client.executeTool(toolExecutionRequest);
                        };


                        // 3. 将规格和执行逻辑成对添加到结果中
                        resultBuilder.add(specification, executor);
                    }
                } catch (Exception e) {
                    // 打印错误，防止某个 MCP 服务挂了导致整个智能体崩溃
                    log.error("无法从 MCP Client 获取工具列表: ", e);
                }
            }
        }

        return resultBuilder.build();
    }
}