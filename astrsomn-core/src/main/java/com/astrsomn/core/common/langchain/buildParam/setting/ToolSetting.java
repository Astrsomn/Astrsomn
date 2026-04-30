package com.astrsomn.core.common.langchain.buildParam.setting;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ToolSetting {
    /**
     * 可用工具列表 (Function Calling)
     */
    private List<String> toolKeys;

    /**
     * 可用 MCP (Model Context Protocol) 列表
     */
    private List<String> mcpKeys;

    /**
     * RAG (检索增强生成) 知识库 ID 列表
     */
    private List<String> ragKeys;

    /**
     * 向量维度 (通常由知识库决定，若需动态指定可保留)
     */
    private Integer vectorSize;


}
