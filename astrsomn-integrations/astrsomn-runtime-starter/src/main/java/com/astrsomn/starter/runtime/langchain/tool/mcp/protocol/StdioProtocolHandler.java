package com.astrsomn.starter.runtime.langchain.tool.mcp.protocol;

import com.astrsomn.api.runtime.common.constant.AiMcpEnum;
import com.astrsomn.api.runtime.common.entity.AiMcpEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class StdioProtocolHandler implements McpProtocolHandler {
    private static final String LOG_PREFIX = "[Astrsomn] [STDIO处理器] ====> ";
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(String type) {
        return AiMcpEnum.TypeEnum.STDIO.getCode().equalsIgnoreCase(type);
    }

    @Override
    public McpTransport createTransport(AiMcpEntity config) throws IOException {
        String commandBase = Optional.ofNullable(config.getCommand())
                .filter(StringUtils::hasText)
                .orElseThrow(() -> new IllegalStateException(LOG_PREFIX + "本地进程启动命令不能为空"));

        List<String> fullCommand = new ArrayList<>();
        fullCommand.add(commandBase);

        // 1. 填充参数
        Optional.ofNullable(config.getArgs())
                .filter(StringUtils::hasText)
                .ifPresent(args -> fullCommand.addAll(parseArgs(args)));

        // 2. 填充环境变量
        Map<String, String> envVars = Optional.ofNullable(config.getEnvVars())
                .filter(StringUtils::hasText)
                .map(this::parseEnvVars)
                .orElse(Collections.emptyMap());

        log.info("{} 构建传输层 | 命令: {} | 环境变量数: {}", LOG_PREFIX, String.join(" ", fullCommand), envVars.size());

        return new StdioMcpTransport.Builder()
                .command(fullCommand)
                .environment(envVars)
                .logEvents(false)
                .build();
    }

    private List<String> parseArgs(String argsJson) {
        try {
            return objectMapper.readValue(argsJson, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.warn("{} 参数 JSON 解析失败，回退至空格分割 | 原始值: {}", LOG_PREFIX, argsJson);
            return Arrays.asList(argsJson.split("\\s+"));
        }
    }

    private Map<String, String> parseEnvVars(String envJson) {
        try {
            return objectMapper.readValue(envJson, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("{} 环境变量解析失败 | 原始值: {}", LOG_PREFIX, envJson);
            return Collections.emptyMap();
        }
    }
}
