package org.astrsomn.starter.langchain.tool.mcp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.entity.AiMcpEntity;
import org.astrsomn.starter.langchain.tool.mcp.protocol.McpProtocolHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class McpToolCacheManager {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Long, McpClient> clientCache = new ConcurrentHashMap<>();
    private final Map<Long, List<ToolSpecification>> toolCache = new ConcurrentHashMap<>();
    private final List<McpProtocolHandler> protocolHandlers;

    public McpClient getOrCreateClient(AiMcpEntity config) {
        return clientCache.computeIfAbsent(config.getId(), id -> {
            log.info("正在初始化 MCP 客户端: {} (类型: {})", config.getServerName(), config.getType());
            McpClient client = createClientInternal(config);
            refreshToolCache(id, client);
            return client;
        });
    }


    public List<ToolSpecification> getCachedTools(Long mcpId) {
        return toolCache.getOrDefault(mcpId, Collections.emptyList());
    }

    public void refreshToolCache(Long mcpId, McpClient client) {
        try {
            List<ToolSpecification> specifications = client.listTools();
            toolCache.put(mcpId, specifications);
            log.info("MCP [ID: {}] 工具列表刷新成功，共 {} 个工具", mcpId, specifications.size());
        } catch (Exception e) {
            log.error("无法从 MCP 客户端获取工具列表 [ID: {}]", mcpId, e);
        }
    }

    private McpClient createClientInternal(AiMcpEntity config) {
        // 使用 Optional 风格寻找匹配的协议处理器
        McpTransport transport = protocolHandlers.stream()
                .filter(handler -> handler.supports(config.getType()))
                .findFirst()
                .map(handler -> {
                    try {
                        return handler.createTransport(config);
                    } catch (IOException e) {
                        throw new RuntimeException("MCP 传输层创建失败", e);
                    }
                })
                .orElseThrow(() -> new UnsupportedOperationException("不支持的 MCP 类型: " + config.getType()));

        return new DefaultMcpClient.Builder()
                .transport(transport)
                .toolExecutionTimeout(java.time.Duration.ofSeconds(30))
                .build();
    }

    // 移除并关闭客户端（如数据库记录删除时调用）
    public void removeClient(Long mcpId) {
        toolCache.remove(mcpId);
        McpClient client = clientCache.remove(mcpId);
        if (client != null) {
            try {
                client.close();
                log.info("已关闭 MCP 客户端 [ID: {}]", mcpId);
            } catch (Exception e) {
                log.error("关闭 MCP 客户端时出错 [ID: {}]", mcpId, e);
            }
        }
    }

    // Spring 容器销毁前关闭所有连接，释放本地进程或 HTTP 连接
    @PreDestroy
    public void destroy() {
        log.info("正在关闭所有 MCP 客户端资源...");
        clientCache.keySet().forEach(this::removeClient);
    }
}