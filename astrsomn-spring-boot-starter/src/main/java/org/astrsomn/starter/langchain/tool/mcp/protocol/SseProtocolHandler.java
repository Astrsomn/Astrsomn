package org.astrsomn.starter.langchain.tool.mcp.protocol;

import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import org.astrsomn.core.common.constant.AiMcpEnum;
import org.astrsomn.core.common.entity.AiMcpEntity;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class SseProtocolHandler implements McpProtocolHandler {
    @Override
    public boolean supports(String type) {
        return AiMcpEnum.TypeEnum.SSE.getCode().equalsIgnoreCase(type);
    }

    @Override
    public McpTransport createTransport(AiMcpEntity config) {
        if (!StringUtils.hasText(config.getSseAddress())) {
            throw new IllegalStateException("SSE 地址不能为空");
        }
        return new HttpMcpTransport.Builder()
                .sseUrl(config.getSseAddress())
                .logRequests(true)
                .build();
    }
}
