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
@EqualsAndHashCode(callSuper = true)
@TableName("AI_TRACE_LOG")
public class AiTraceLogEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 链路追踪全局 ID (UUID)
     */
    @TableField("TRACE_ID")
    private String traceId;

    /**
     * 关联对话表 ID (AI_CONVERSATION.ID)
     */
    @TableField("CONVERSATION_ID")
    private Long conversationId;

    /**
     * 关联 Agent ID (AI_AGENT.ID)
     * 注意：这里存的是 ID，即使 Agent 逻辑删除了，ID 依然能对上
     */
    @TableField("AGENT_ID")
    private Long agentId;

    /**
     * 关联模型实例 ID (AI_INSTANCE.ID)
     */
    @TableField("INSTANCE_ID")
    private Long instanceId;

    /**
     * 关联提示词 ID (AI_PROMPT.ID)
     */
    @TableField("PROMPT_ID")
    private Long promptId;

    /**
     * 冗余存储当时的关键 Key (方便在不查关联表的情况下快速筛选)
     */
    @TableField("AGENT_KEY")
    private String agentKey;

    /**
     * 运行快照 (JSON)
     * 即使有关联 ID，建议还是把最核心的 Prompt 文本冗余存一份在这里
     * 这样即便清理了历史数据，日志回放依然可用
     */
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
