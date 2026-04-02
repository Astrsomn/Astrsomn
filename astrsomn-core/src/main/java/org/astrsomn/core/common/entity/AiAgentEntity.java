package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;


@Data
@TableName("AI_AGENT")
public class AiAgentEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("AGENT_KEY")
    private String agentKey;

    @TableField("AGENT_NAME")
    private String agentName;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("WORKFLOW_KEY")
    private String workflowKey;


    @TableField("CHAT_INSTANCE_KEY")
    private String chatInstanceKey;

    @TableField("EMBEDDING_INSTANCE_KEY")
    private String embeddingInstanceKey;

    @TableField("IMAGE_INSTANCE_KEY")
    private String imageInstanceKey;

    @TableField("PROMPT_KEY")
    private String promptKey;

    @TableField("KNOWLEDGE_BASE_KEYS")
    private String knowledgeBaseKeys;

    @TableField("TOOL_KEYS")
    private String toolKeys;

    @TableField("MCP_KEYS")
    private String mcpKeys;

    @TableField("STATUS")
    private String status;

    @TableField("MEMORY_MODE")
    private String memoryMode;

    @TableField("MEMORY_WINDOW_SIZE")
    private String memoryWindowSize;

    @TableField("ENABLE_STREAM")
    private boolean enableStream;

    /**
     * 当前环境下是否作为默认智能体（1=是）。同一 {@code ENV_CODE} 建议仅一条，供未传 agentKey 时走通一条链路。
     */
    @TableField("IS_DEFAULT")
    private Integer isDefault;

}
