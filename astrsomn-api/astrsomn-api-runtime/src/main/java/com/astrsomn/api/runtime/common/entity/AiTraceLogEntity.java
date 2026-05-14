package com.astrsomn.api.runtime.common.entity;

import com.astrsomn.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("AI_TRACE_LOG")
public class AiTraceLogEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    @TableField("TRACE_ID")
    private String traceId;


    @TableField("CONVERSATION_ID")
    private Long conversationId;


    @TableField("AGENT_ID")
    private Long agentId;


    @TableField("INSTANCE_ID")
    private Long instanceId;


    @TableField("PROMPT_ID")
    private Long promptId;


    @TableField("AGENT_KEY")
    private String agentKey;


    @TableField("SNAPSHOT_CONTENT")
    private String snapshotContent;

    // --- 执行数据 ---
    @TableField("NODE_TYPE") // LLM, RAG, MCP, TOOL
    private String nodeType;

    @TableField("INPUT_DATA")
    private String inputData;

    @TableField("OUTPUT_DATA")
    private String outputData;

    @TableField("CONSUME_TOKENS")
    private Integer consumeTokens;

    @TableField("DURATION")
    private Long duration;

    @TableField("STATUS") // SUCCESS, FAIL
    private String status;
}