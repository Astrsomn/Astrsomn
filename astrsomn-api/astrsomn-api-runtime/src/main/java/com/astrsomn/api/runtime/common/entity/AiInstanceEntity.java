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

    
    @TableField(exist = false)
    private String modelType;
    
    @TableField("MAX_TOKENS")
    private Integer maxTokens;

    
    @TableField("TEMPERATURE")
    private Double temperature;

    
    @TableField("PRESENCE_PENALTY")
    private Double presencePenalty;

    
    @TableField("FREQUENCY_PENALTY")
    private Double frequencyPenalty;

    
    @TableField("STOP_SEQUENCES")
    private String stopSequences;

    
    @TableField("SEED")
    private Integer seed;

    
    @TableField("TOP_P")
    private Double topP;

    
    @TableField("TOP_K")
    private Integer topK;

    
    @TableField("STYLE")
    private String style;

    
    @TableField("SIZE")
    private String size;

    
    @TableField("DIMENSIONS")
    private Integer dimensions;

    
    @TableField("STATUS")
    private String status;

    
    @TableField("IS_DEFAULT")
    private String isDefault;

    
    @TableField("ACCOUNT_KEY")
    private String accountKey;

}