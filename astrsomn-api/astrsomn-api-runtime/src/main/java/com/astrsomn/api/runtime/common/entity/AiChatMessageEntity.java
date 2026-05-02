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

/**
 * AI 对话消息明细表。
 * 前端每次请求都携带 memoryKey，服务端按 memoryKey 聚合同一条会话链路。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("AI_CHAT_MESSAGE")
public class AiChatMessageEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 会话记忆键（前端传入，作为会话聚合主键）。
     * 建议按 memoryKey + turnNo 升序获取上下文历史。
     *
     */
    @TableField("MEMORY_KEY")
    private String memoryKey;

    /**
     * 会话轮次，从 1 开始递增；一次问答可包含多条消息（user/assistant/tool）。
     */
    @TableField("TURN_NO")
    private Integer turnNo;

    /**
     * 消息顺序（同一轮 turnNo 内从 1 开始）。
     */
    @TableField("MESSAGE_ORDER")
    private Integer messageOrder;

    /**
     * 消息角色：user / assistant / system / tool。
     */
    @TableField("ROLE")
    private String role;

    /**
     * 消息类型：text / reasoning / tool_call / tool_result / error。
     */
    @TableField("MESSAGE_TYPE")
    private String messageType;

    /**
     * 消息内容（建议文本；结构化内容可放 extJson）。
     */
    @TableField("CONTENT")
    private String content;

    /**
     * 回复状态：streaming / completed / failed / interrupted。
     *
     */
    @TableField("RESPONSE_STATUS")
    private String responseStatus;

    /**
     * 结束原因：stop / length / tool_call / content_filter / error。
     */
    @TableField("FINISH_REASON")
    private String finishReason;

    /**
     * 本条消息输入 token 数。
     *
     */
    @TableField("PROMPT_TOKENS")
    private Integer promptTokens;

    /**
     * 本条消息输出 token 数。
     */
    @TableField("COMPLETION_TOKENS")
    private Integer completionTokens;

    /**
     * 本条消息总 token 数（可由 prompt + completion 计算，也可直接入库）。
     */
    @TableField("TOTAL_TOKENS")
    private Integer totalTokens;

    /**
     * 关联链路追踪 ID，便于串联日志。
     */
    @TableField("TRACE_ID")
    private String traceId;

    /**
     * 错误码（失败场景记录，成功可为空）。
     */
    @TableField("ERROR_CODE")
    private String errorCode;

    /**
     * 关联 Agent Key（冗余快照）。
     */
    @TableField("AGENT_KEY")
    private String agentKey;

    /**
     * 关联实例 Key（冗余快照）。
     */
    @TableField("INSTANCE_KEY")
    private String instanceKey;

    /**
     * 关联模型 Key（冗余快照）。
     */
    @TableField("MODEL_KEY")
    private String modelKey;

    /**
     * 关联账号 Key（冗余快照）。
     */
    @TableField("ACCOUNT_KEY")
    private String accountKey;

    /**
     * 关联 Prompt Key（冗余快照）。
     */
    @TableField("PROMPT_KEY")
    private String promptKey;

    /**
     * 扩展信息 JSON（工具参数、结构化输出、埋点等）。
     */
    @TableField("EXT_JSON")
    private String extJson;
}