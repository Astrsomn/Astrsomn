package com.astrsomn.starter.langchain.tool.mcp.protocol;

import dev.langchain4j.mcp.client.transport.McpTransport;
import com.astrsomn.core.common.entity.AiMcpEntity;

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
