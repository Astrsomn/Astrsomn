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

    /**
     * id
     */
    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * The key of Agent
     */
    @TableField("AGENT_KEY")
    private String agentKey;

    /**
     * Optional workflow key: when set, application layer may route chat through workflow orchestration
     * instead of a single-turn assistant call.
     */
    @TableField("WORKFLOW_KEY")
    private String workflowKey;

    /**
     * The name of the Agent.
     */
    @TableField("AGENT_NAME")
    private String agentName;

    /**
     * A detailed description of the Agent's purpose and capabilities.
     */
    @TableField("DESCRIPTION")
    private String description;

    /**
     * The ID of the default model assigned to this Agent.
     */
    @TableField("MODEL_KEY")
    private String modelKey;

    /**
     * The UUID of the system prompt associated with this Agent.
     */
    @TableField("PROMPT_KEY")
    private String promptKey;



    /**
     * The status of the Agent (e.g., ENABLED, DISABLED).
     */
    @TableField("STATUS")
    private String status;

    /**
     * A comma-separated list of IDs for associated knowledge bases.
     */
    @TableField("KNOWLEDGE_BASE_KEYS")
    private String knowledgeBaseKeys;

    /**
     * A comma-separated list of IDs for tools enabled for this Agent.
     */
    @TableField("TOOL_KEYS")
    private String toolKeys;

    /**
     * A comma-separated list of IDs for Model Context Protocol (MCP) servers.
     */
    @TableField("MCP_KEYS")
    private String mcpKeys;

    /**
     * The memory mode strategy (e.g., NONE, SLIDING_WINDOW, VECTOR).
     */
    @TableField("MEMORY_MODE")
    private String memoryMode;

    /**
     * The number of recent conversation turns to retain in memory (window size).
     */
    @TableField("MEMORY_WINDOW_SIZE")
    private String memoryWindowSize;


    /**
     * Enables streaming response output if set to true.
     */
    @TableField("ENABLE_STREAM")
    private boolean enableStream;

    /**
     *
     */
    @TableField("STYLE")
    private String style;

    /**
     *
     */
    @TableField("SIZE")
    private String size;

    /**
     *
     */
    @TableField("DIMENSIONS")
    private Integer dimensions;
    /**
     *
     */
    @TableField("EMBEDDING_MODEL_KEY")
    private String embeddingModelKey;

    /**
     *
     */
    @TableField("IMAGE_MODEL_KEY")
    private String imageModelKey;
}
