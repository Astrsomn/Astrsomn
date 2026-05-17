package com.astrsomn.starter.runtime.langchain.tool.mcp.protocol;

import com.astrsomn.api.runtime.common.constant.AiMcpEnum;
import com.astrsomn.api.runtime.common.entity.AiMcpEntity;
import com.astrsomn.starter.runtime.langchain.exception.ErrorCode;
import com.astrsomn.starter.runtime.langchain.exception.McpConnectionException;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import org.springframework.util.StringUtils;

public class SseProtocolHandler implements McpProtocolHandler {
    @Override
    public boolean supports(String type) {
        return AiMcpEnum.TypeEnum.SSE.getCode().equalsIgnoreCase(type);
    }

    @Override
    public McpTransport createTransport(AiMcpEntity config) {
        if (!StringUtils.hasText(config.getSseAddress())) {
            throw new McpConnectionException(ErrorCode.MCP_SSE_URL_EMPTY, "SSE URL must not be empty");
        }
        return new HttpMcpTransport.Builder()
                .sseUrl(config.getSseAddress())
                .logRequests(true)
                .build();
    }
}
