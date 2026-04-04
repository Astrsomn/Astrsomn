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

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("AI_INSTANCE")
public class AiInstanceEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("INSTANCE_KEY")
    private String instanceKey;

    @TableField("INSTANCE_NAME")
    private String instanceName;

    @TableField("MODEL_KEY")
    private String modelKey;

    /**
     * Resolved from {@code AI_MODEL.MODEL_TYPE} by {@code modelKey} + {@code envCode}; not a column on {@code AI_INSTANCE}.
     */
    @TableField(exist = false)
    private String modelType;
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
    @TableField("STATUS")
    private String status;




}
