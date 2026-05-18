package com.astrsomn.starter.runtime.langchain.tool.mcp.protocol;

import com.astrsomn.api.runtime.common.entity.AiMcpEntity;
import dev.langchain4j.mcp.client.transport.McpTransport;

import java.io.IOException;

public interface McpProtocolHandler {


    
    boolean supports(String type);

    
    McpTransport createTransport(AiMcpEntity config) throws IOException;

}
