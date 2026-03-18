package org.astrsomn.core.common.langchain.buildParam;

import lombok.Data;

import java.util.List;

@Data
public class ToolStrategy {
    /**
     * 可用工具列表 (Function Calling)
     */
    private List<Long> toolIds;

    /**
     * 可用 MCP (Model Context Protocol) 列表
     */
    private List<Long> mcpIds;

    /**
     * RAG (检索增强生成) 知识库 ID 列表
     */
    private List<Long> ragIds;

    /**
     * 向量维度 (通常由知识库决定，若需动态指定可保留)
     */
    private Integer vectorSize;


}
