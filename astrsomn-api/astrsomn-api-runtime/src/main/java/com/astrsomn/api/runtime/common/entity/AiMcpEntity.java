package com.astrsomn.api.runtime.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.astrsomn.common.base.BaseEntity;


@Data
@EqualsAndHashCode(callSuper = false)
@TableName("AI_MCP")
public class AiMcpEntity extends BaseEntity<Long> {

    
    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    
    @TableField("MCP_KEY")
    private String mcpKey;
    
    @TableField("SERVER_NAME")
    private String serverName;

    
    @TableField("DESCRIPTION")
    private String description;

    
    @TableField("TYPE")
    private String type;

    
    @TableField("SSE_ADDRESS")
    private String sseAddress;

    
    @TableField("REQUEST_HEADER_CONFIG")
    private String requestHeaderConfig;

    
    @TableField("ENABLED")
    private Integer enabled;

    
    @TableField("COMMAND")
    private String command;

    
    @TableField("ARGS")
    private String args;

    
    @TableField("ENV_VARS")
    private String envVars;
}