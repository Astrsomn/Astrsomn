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
@TableName("AI_CHAT_MESSAGE")
public class AiChatMessageEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    
    @TableField("MEMORY_KEY")
    private String memoryKey;

    
    @TableField("TURN_NO")
    private Integer turnNo;

    
    @TableField("MESSAGE_ORDER")
    private Integer messageOrder;

    
    @TableField("ROLE")
    private String role;

    
    @TableField("MESSAGE_TYPE")
    private String messageType;

    
    @TableField("CONTENT")
    private String content;

    
    @TableField("RESPONSE_STATUS")
    private String responseStatus;

    
    @TableField("FINISH_REASON")
    private String finishReason;

    
    @TableField("PROMPT_TOKENS")
    private Integer promptTokens;

    
    @TableField("COMPLETION_TOKENS")
    private Integer completionTokens;

    
    @TableField("TOTAL_TOKENS")
    private Integer totalTokens;

    
    @TableField("TRACE_ID")
    private String traceId;

    
    @TableField("ERROR_CODE")
    private String errorCode;

    
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

    
    @TableField("EXT_JSON")
    private String extJson;
}