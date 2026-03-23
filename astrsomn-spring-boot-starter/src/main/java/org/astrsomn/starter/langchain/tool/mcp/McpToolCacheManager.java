package org.astrsomn.starter.langchain.tool.mcp;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.entity.AiMcpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class McpToolCacheManager {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 客户端连接缓存: Key 为数据库 ID
    private final Map<Long, McpClient> clientCache = new ConcurrentHashMap<>();

    // 工具规格缓存: 避免每次对话都通过网络/进程请求工具列表
    private final Map<Long, List<ToolSpecification>> toolCache = new ConcurrentHashMap<>();

    /**
     * 获取或创建 MCP 客户端
     */
    public McpClient getOrCreateClient(AiMcpEntity config) {
        if (config == null || config.getId() == null) {
            throw new IllegalArgumentException("MCP 配置无效");
        }

        return clientCache.computeIfAbsent(config.getId(), id -> {
            try {
                log.info("正在初始化 MCP 客户端: {} (类型: {})", config.getServerName(), config.getType());
                McpClient client = createClientInternal(config);

                // 启动后立即异步/同步拉取一次工具列表
                refreshToolCache(id, client);

                return client;
            } catch (Exception e) {
                log.error("创建 MCP 客户端失败 [ID: {}]", id, e);
                throw new RuntimeException("MCP 客户端初始化异常", e);
            }
        });
    }

    /**
     * 获取缓存中的工具列表
     */
    public List<ToolSpecification> getCachedTools(Long mcpId) {
        return toolCache.getOrDefault(mcpId, Collections.emptyList());
    }

    /**
     * 刷新特定客户端的工具列表
     */
    public void refreshToolCache(Long mcpId, McpClient client) {
        try {
            // 注意：listTools() 在某些传输协议下可能是阻塞操作
            List<ToolSpecification> specifications = client.listTools();
            toolCache.put(mcpId, specifications);
            log.info("MCP [ID: {}] 工具列表刷新成功，共 {} 个工具", mcpId, specifications.size());
        } catch (Exception e) {
            log.error("无法从 MCP 客户端获取工具列表 [ID: {}]", mcpId, e);
        }
    }

    private McpClient createClientInternal(AiMcpEntity config) throws IOException {
        McpTransport transport;

        if ("SSE".equalsIgnoreCase(config.getType())) {
            transport = createSseTransport(config);
        } else if ("STDIO".equalsIgnoreCase(config.getType())) {
            transport = createStdioTransport(config);
        } else {
            throw new UnsupportedOperationException("不支持的 MCP 类型: " + config.getType());
        }

        return new DefaultMcpClient.Builder()
                .transport(transport)
                .toolExecutionTimeout(java.time.Duration.ofSeconds(30)) // 可根据需要配置超时
                .build();
    }

    private McpTransport createSseTransport(AiMcpEntity config) {
        if (!StringUtils.hasText(config.getSseAddress())) {
            throw new IllegalStateException("SSE 地址不能为空");
        }
        return new HttpMcpTransport.Builder()
                .sseUrl(config.getSseAddress())
                .logRequests(true)
                .build();
    }

    private McpTransport createStdioTransport(AiMcpEntity config) throws IOException {
        if (!StringUtils.hasText(config.getCommand())) {
            throw new IllegalStateException("STDIO 命令不能为空");
        }

        // 1. 组装命令
        List<String> command = new ArrayList<>();
        command.add(config.getCommand());

        // 2. 解析参数 (JSON 数组格式)
        if (StringUtils.hasText(config.getArgs())) {
            try {
                List<String> args = objectMapper.readValue(config.getArgs(), new TypeReference<>() {});
                command.addAll(args);
            } catch (Exception e) {
                log.warn("MCP 参数解析失败，尝试按空格分割: {}", config.getArgs());
                command.addAll(Arrays.asList(config.getArgs().split("\\s+")));
            }
        }

        // 3. 解析环境变量 (JSON 对象格式)
        Map<String, String> environment = new HashMap<>();
        if (StringUtils.hasText(config.getEnvVars())) {
            try {
                environment = objectMapper.readValue(config.getEnvVars(), new TypeReference<>() {});
            } catch (Exception e) {
                log.error("环境变量解析失败: {}", config.getEnvVars());
            }
        }

        return new StdioMcpTransport.Builder()
                .command(command)
                .environment(environment)
                .logEvents(false) // 生产环境建议关闭
                .build();
    }

    /**
     * 移除并关闭客户端（如数据库记录删除时调用）
     */
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

    /**
     * Spring 容器销毁前关闭所有连接，释放本地进程或 HTTP 连接
     */
    @PreDestroy
    public void destroy() {
        log.info("正在关闭所有 MCP 客户端资源...");
        clientCache.keySet().forEach(this::removeClient);
    }
}