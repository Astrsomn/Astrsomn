package org.astrsomn.starter.langchain.mcp;

import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.AiMcpEnum;
import org.astrsomn.core.common.entity.AiMcpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 推荐使用 Jackson 进行 JSON 解析 (Spring Boot 默认集成)
// 如果没有 Jackson，也可以手动解析字符串，避免引入错误的 JSONArray
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class McpManager {

    private final Map<Long, McpClient> clientCache = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取或创建 MCP 客户端
     * 注意：此方法假设配置一旦创建就不会变更。如果配置会变，需要增加版本校验或移除旧缓存。
     */
    public McpClient getOrCreateClient(AiMcpEntity config) {
        if (config == null || config.getId() == null) {
            throw new IllegalArgumentException("MCP 配置不能为空");
        }

        return clientCache.computeIfAbsent(config.getId(), id -> {
            try {
                String type = config.getType();

                if (AiMcpEnum.TypeEnum.SSE.getCode().equalsIgnoreCase(type)) {
                    return createSseClient(config);
                }
                else if (AiMcpEnum.TypeEnum.STDIO.getCode().equalsIgnoreCase(type)) {
                    return createStdioClient(config);
                }
                else {
                    throw new UnsupportedOperationException("不支持的 MCP 类型: " + type);
                }
            } catch (Exception e) {
                log.error("创建 MCP 客户端失败 [ID: {}]", config.getId(), e);
                throw new RuntimeException("初始化 MCP 客户端失败", e);
            }
        });
    }

    /**
     * 构建 SSE (HTTP) 客户端
     */
    private McpClient createSseClient(AiMcpEntity config) {
        if (!StringUtils.hasText(config.getSseAddress())) {
            throw new IllegalStateException("SSE 类型的 MCP 必须配置 sseAddress");
        }

        log.info("初始化 SSE MCP 客户端: {}", config.getServerName());

        McpTransport transport = new HttpMcpTransport.Builder()
                .sseUrl(config.getSseAddress())
                // 如果有认证头，可以在这里添加 .httpHeaders(...)
                .build();

        return new DefaultMcpClient.Builder()
                .transport(transport)
                .build();
    }

    /**
     * 构建 STDIO (进程) 客户端
     */
    private McpClient createStdioClient(AiMcpEntity config) throws IOException {
        if (!StringUtils.hasText(config.getCommand())) {
            throw new IllegalStateException("STDIO 类型的 MCP 必须配置 command");
        }

        log.info("初始化 STDIO MCP 客户端: {}", config.getServerName());

        // 1. 构建命令列表: [executable, arg1, arg2, ...]
        List<String> commands = new ArrayList<>();
        commands.add(config.getCommand());

        // 2. 解析参数字符串 (假设 config.getArgs() 是 JSON 数组字符串，如 '["--port", "8080"]')
        if (StringUtils.hasText(config.getArgs())) {
            try {
                // 使用 Jackson 安全地解析为 List<String>
                List<String> argsList = objectMapper.readValue(config.getArgs(), new TypeReference<List<String>>() {});
                if (argsList != null) {
                    commands.addAll(argsList);
                }
            } catch (Exception e) {
                log.warn("解析 MCP 参数失败，尝试按空格分割字符串: {}", config.getArgs(), e);
                // 降级方案：如果不是标准 JSON，尝试按空格分割（视具体业务而定）
                // 或者干脆抛出异常，强制要求格式正确
                String[] splitArgs = config.getArgs().split("\\s+");
                for (String arg : splitArgs) {
                    if (!arg.isEmpty()) commands.add(arg);
                }
            }
        }

        // 3. 构建环境变量 (不要把它当成命令参数！)
        Map<String, String> environment = Map.of();
        if (StringUtils.hasText(config.getEnvVars())) {
            try {
                // 假设 envVars 是 JSON 对象字符串，如 '{"KEY": "VALUE"}'
                environment = objectMapper.readValue(config.getEnvVars(), new TypeReference<Map<String, String>>() {});
            } catch (Exception e) {
                log.error("解析 MCP 环境变量失败: {}", config.getEnvVars(), e);
                // 可以选择忽略或抛出异常
            }
        }

        StdioMcpTransport transport = new StdioMcpTransport.Builder()
                .command(commands)
                .environment(environment) // 正确设置环境变量
                .logEvents(true) // 生产环境建议关闭或改为 debug 级别日志
                .build();

        return new DefaultMcpClient.Builder()
                .transport(transport)
                .build();
    }

    /**
     * 【重要】提供关闭客户端的方法，防止资源泄露
     * 当配置更新或删除 MCP 服务时调用
     */
    public void removeClient(Long mcpId) {
        McpClient client = clientCache.remove(mcpId);
        if (client != null) {
            try {
                client.close(); // LangChain4j 的 Closeable
                log.info("已关闭并移除 MCP 客户端: {}", mcpId);
            } catch (Exception e) {
                log.error("关闭 MCP 客户端时出错: {}", mcpId, e);
            }
        }
    }

    /**
     * 关闭所有客户端 (应用停止时调用)
     */
    public void destroy() {
        clientCache.forEach((id, client) -> {
            try {
                client.close();
            } catch (Exception e) {
                log.error("关闭 MCP 客户端失败: {}", id, e);
            }
        });
        clientCache.clear();
    }
}