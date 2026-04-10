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
    @TableField("PROVIDER")
    private String provider;

    /**
     * AccountKey
     */
    @TableField("ACCOUNT_KEY")
    private String accountKey;

    /**
     * API URL
     */
    @TableField("API_URL")
    private String apiUrl;

    /**
     * Status (enabled-disabled)
     */
    @TableField("STATUS")
    private String status;


    /**
     * Is default model
     */
    @TableField("IS_DEFAULT")
    private Integer isDefault;
    
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


}