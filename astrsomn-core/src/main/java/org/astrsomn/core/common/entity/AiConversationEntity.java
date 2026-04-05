package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.astrsomn.core.common.base.BaseEntity;


/**
 * Entity representing a single message within an AI conversation session.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("AI_CONVERSATION")
public class AiConversationEntity extends BaseEntity<Long> {

    /**
     * Primary key ID.
     */
    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     *
     */
    @TableField("MEMORY_KEY")
    private String memoryKey;
    /**
     * The role of the message sender.
     * Expected values: 'user' or 'assistant'.
     */
    @TableField("ROLE")
    private String role;

    /**
     * The content of the message (either user input or model response).
     */
    @TableField("CONTENT")
    private String content;

    /**
     * The sequential order of this message within the conversation session.
     * Zero-based index (starts from 0).
     */
    @TableField("MESSAGE_ORDER")
    private Integer messageOrder;

    /**
     * The total number of tokens consumed for generating or processing this message.
     */
    @TableField("CONSUME_TOKENS")
    private Integer consumeTokens;

    /**
     * The ID of the Agent associated with this conversation.
     */
    @TableField("AGENT_KEY")
    private String agentKey;

    /**
     *
     */
    @TableField("INSTANCE_KEY")
    private String instanceKey;

    /**
     * The ID of the model used to generate this response.
     */
    @TableField("MODEL_KEY")
    private String modelKey;
    /**
     *
     */
    @TableField("ACCOUNT_KEY")
    private String accountKey;
    /**
     * The ID of the prompt or template associated with this conversation.
     */
    @TableField("PROMPT_KEY")
    private String promptKey;
}