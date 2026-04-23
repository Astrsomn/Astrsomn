package com.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.astrsomn.core.common.base.BaseEntity;

/**
 * AI MCP configuration entity (server name, type SSE/STDIO/STEAMABLE, SSE address, request headers, etc.)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("AI_MCP")
public class AiMcpEntity extends BaseEntity<Long> {

    /**
     * id
     */
    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * mcpKey
     */
    @TableField("MCP_KEY")
    private String mcpKey;
    /**
     * Server name
     */
    @TableField("SERVER_NAME")
    private String serverName;

    /**
     * Description
     */
    @TableField("DESCRIPTION")
    private String description;

    /**
     * Type: SSE / STEAMABLE / STDIO
     */
    @TableField("TYPE")
    private String type;

    /**
     * SSE address (required when type is SSE)
     */
    @TableField("SSE_ADDRESS")
    private String sseAddress;

    /**
     * Request header configuration (e.g., JSON string)
     */
    @TableField("REQUEST_HEADER_CONFIG")
    private String requestHeaderConfig;

    /**
     * Enabled status: 1-yes 0-no
     */
    @TableField("ENABLED")
    private Integer enabled;

    /**
     * [New field] STDIO run command (e.g., "npx", "python", "node")
     */
    @TableField("COMMAND")
    private String command;

    /**
     * [New field] Run arguments (e.g., "-y", "@modelcontextprotocol/server-everything")
     * Recommended to store as JSON array string or space-separated
     */
    @TableField("ARGS")
    private String args;

    /**
     * [New field] Environment variables (e.g., {"API_KEY": "xxx"})
     * Recommended to store as JSON string
     */
    @TableField("ENV_VARS")
    private String envVars;
}
