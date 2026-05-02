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

    /**
     * 会话聚合键，前端每次请求均携带该值。
     */
    @TableField("MEMORY_KEY")
    private String memoryKey;

    /**
     * 会话标题（可由首条消息摘要生成）。
     */
    @TableField("SESSION_TITLE")
    private String sessionTitle;

    /**
     * 会话状态：active / archived / deleted。
     */
    @TableField("SESSION_STATUS")
    private String sessionStatus;

    /**
     * 最后一条消息预览。
     */
    @TableField("LAST_MESSAGE_PREVIEW")
    private String lastMessagePreview;

    /**
     * 最近一条消息时间戳，毫秒级。
     */
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
