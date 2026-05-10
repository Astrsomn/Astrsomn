package com.astrsomn.api.runtime.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.astrsomn.common.base.BaseEntity;

import java.time.LocalDateTime;

/**
 * AI model configuration entity
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("AI_MODEL")
public class AiModelEntity extends BaseEntity<Long> {
    /**
     * id
     */
    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * Model Key
     */
    @TableField("MODEL_KEY")
    private String modelKey;

    /**
     * Model name
     */
    @TableField("MODEL_NAME")
    private String modelName;

    /**
     * Model type (ChatModel ImageModel EmbeddingModel)
     */
    @TableField("MODEL_TYPE")
    private String modelType;

    /**
     * Provider
     */
    @TableField("EXTENSION_CODE")
    private String extensionCode;

    /**
     * Status (enabled-disabled)
     */
    @TableField("STATUS")
    private String status;


    /**
     * Is default model
     */
    @TableField("IS_DEFAULT")
    private String isDefault;
    
    /**
     * Capability classification (stored in JSON format)
     */
    @TableField("CAPABILITIES")
    private String capabilities;

    /**
     * Params
     */
    @TableField("PARAMS")
    private String params;
    /**
     * token使用总量限制
     */
    @TableField("MAX_QUOTA_TOKENS")
    private Long maxQuotaTokens;


    @TableField("SOURCE_TYPE")
    private String sourceType;


}