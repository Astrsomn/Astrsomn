package org.astrsomn.starter.langchain.tool.mcp.protocol;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.entity.AiMcpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
@Slf4j
@Component
@RequiredArgsConstructor
public class StdioProtocolHandler implements McpProtocolHandler {
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(String type) {
        return "STDIO".equalsIgnoreCase(type);
    }

    @Override
    public McpTransport createTransport(AiMcpEntity config) throws IOException {
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
}
