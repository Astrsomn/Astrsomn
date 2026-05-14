package com.astrsomn.starter.runtime.langchain.tool.mcp.protocol;

import com.astrsomn.api.runtime.common.entity.AiMcpEntity;
import dev.langchain4j.mcp.client.transport.McpTransport;

import java.io.IOException;

public interface McpProtocolHandler {


    /**
     * 判断当前处理器是否支持该类型 (如 SSE, STDIO)
     */
    boolean supports(String type);

    /**
     * 根据配置创建对应的传输层
     */
    McpTransport createTransport(AiMcpEntity config) throws IOException;

}
