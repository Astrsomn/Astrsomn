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
@TableName("AI_CHAT_SESSION")
public class AiChatSessionEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    @TableField("MEMORY_KEY")
    private String memoryKey;


    @TableField("SESSION_TITLE")
    private String sessionTitle;


    @TableField("SESSION_STATUS")
    private String sessionStatus;


    @TableField("LAST_MESSAGE_PREVIEW")
    private String lastMessagePreview;


    @TableField("LAST_MESSAGE_AT")
    private Long lastMessageAt;

    @TableField("MESSAGE_COUNT")
    private Integer messageCount;

    @TableField("PROMPT_TOKENS")
    private Integer promptTokens;

    @TableField("COMPLETION_TOKENS")
    private Integer completionTokens;

    @TableField("TOTAL_TOKENS")
    private Integer totalTokens;

    @TableField("AGENT_KEY")
    private String agentKey;

    @TableField("INSTANCE_KEY")
    private String instanceKey;

    @TableField("MODEL_KEY")
    private String modelKey;

    @TableField("ACCOUNT_KEY")
    private String accountKey;

    @TableField("PROMPT_KEY")
    private String promptKey;

    @TableField("TRACE_ID")
    private String traceId;

    @TableField("EXT_JSON")
    private String extJson;
}