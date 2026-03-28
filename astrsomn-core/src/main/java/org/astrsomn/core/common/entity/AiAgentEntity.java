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
     * Runtime configuration parameters in JSON format.
     * Example: { "temperature": 0.7, "max_tokens": 2048, ... }
     */
    @TableField("CONFIG_PARAMS")
    private String configParams;

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
     * The maximum number of tokens allowed in the generated response.
     */
    @TableField("MAX_TOKENS")
    private Integer maxTokens;

    /**
     * The temperature value controlling the randomness of the output.
     */
    @TableField("TEMPERATURE")
    private Double temperature;

    /**
     * The presence penalty value to discourage token repetition based on existence.
     */
    @TableField("PRESENCE_PENALTY")
    private Double presencePenalty;

    /**
     * The frequency penalty value to discourage token repetition based on count.
     */
    @TableField("FREQUENCY_PENALTY")
    private Double frequencyPenalty;

    /**
     * Custom sequences that will trigger the end of text generation.
     */
    @TableField("STOP_SEQUENCES")
    private String stopSequences;

    /**
     * The random seed for reproducible output generation.
     */
    @TableField("SEED")
    private Integer seed;

    /**
     *
     */
    @TableField("TOP_P")
    private Double topP;

    /**
     *
     */
    @TableField("TOP_K")
    private Integer topK;
    /**
     * Enables streaming response output if set to true.
     */
    @TableField("ENABLE_STREAM")
    private boolean enableStream;


}
