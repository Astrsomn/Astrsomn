package com.astrsomn.starter.langchain.tool.mcp;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.core.common.entity.AiMcpEntity;
import com.astrsomn.starter.langchain.tool.mcp.protocol.McpProtocolHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class McpToolCacheManager {

    private static final String LOG_PREFIX = "[Astrsomn] [MCP注册中心] ====> ";

    private final Map<Long, McpClient> clientCache = new ConcurrentHashMap<>();
    private final Map<Long, List<ToolSpecification>> toolCache = new ConcurrentHashMap<>();
    private final List<McpProtocolHandler> protocolHandlers;

    /**
     * 获取或创建 MCP 客户端，并同步刷新工具缓存
     */
    public McpClient getOrCreateClient(AiMcpEntity config) {
        return clientCache.computeIfAbsent(config.getId(), id -> {
            log.info("{} 初始化客户端 | 名称: {} | 类型: {}", LOG_PREFIX, config.getServerName(), config.getType());
            McpClient client = createClientInternal(config);
            refreshTools(id, client);
            return client;
        });
    }

    /**
     * 获取已缓存的工具列表规格
     */
    public List<ToolSpecification> getCachedTools(Long mcpId) {
        return toolCache.getOrDefault(mcpId, Collections.emptyList());
    }

    /**
     * 刷新特定客户端的工具元数据缓存
     */
    public void refreshTools(Long mcpId, McpClient client) {
        try {
            List<ToolSpecification> specs = client.listTools();
            toolCache.put(mcpId, specs);
            log.info("{} 工具缓存刷新成功 | ID: {} | 数量: {}", LOG_PREFIX, mcpId, specs.size());
        } catch (Exception e) {
            log.error("{} 获取工具列表失败 | ID: {} | 错误: {}", LOG_PREFIX, mcpId, e.getMessage());
        }
    }

    /**
     * 移除并销毁客户端
     */
    public void removeClient(Long mcpId) {
        toolCache.remove(mcpId);
        Optional.ofNullable(clientCache.remove(mcpId)).ifPresent(client -> {
            try {
                client.close();
                log.info("{} 已成功关闭客户端资源 | ID: {}", LOG_PREFIX, mcpId);
            } catch (Exception e) {
                log.error("{} 关闭客户端异常 | ID: {} | 错误: {}", LOG_PREFIX, mcpId, e.getMessage());
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        log.info("{} 正在释放所有 MCP 连接资源...", LOG_PREFIX);
        clientCache.keySet().forEach(this::removeClient);
    }

    private McpClient createClientInternal(AiMcpEntity config) {
        McpTransport transport = protocolHandlers.stream()
                .filter(h -> h.supports(config.getType()))
                .findFirst()
                .map(h -> {
                    try {
                        return h.createTransport(config);
                    } catch (IOException e) {
                        throw new RuntimeException("MCP Transport creation failed", e);
                    }
                })
                .orElseThrow(() -> new UnsupportedOperationException("不支持的 MCP 类型: " + config.getType()));

        return new DefaultMcpClient.Builder()
                .transport(transport)
                .toolExecutionTimeout(Duration.ofSeconds(30))
                .build();
    }
}