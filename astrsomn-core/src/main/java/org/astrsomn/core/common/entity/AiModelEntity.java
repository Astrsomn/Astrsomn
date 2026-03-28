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
     * Model name
     */
    @TableField("MODEL_NAME")
    private String modelName;

    /**
     * Model Key
     */
    @TableField("MODEL_KEY")
    private String modelKey;
    /**
     * Model type
     */
    @TableField("MODEL_TYPE")
    private String modelType;

    /**
     * Provider
     */
    @TableField("PROVIDER")
    private String provider;
    
    /**
     * APIKey
     */
    @TableField("API_KEY")
    private String apiKey;

    /**
     * API secret key
     */
    @TableField("API_SECRET")
    private String apiSecret;

    /**
     * API URL
     */
    @TableField("API_URL")
    private String apiUrl;
    /**
     * Model parameters
     */
    @TableField("MODEL_PARAMS")
    private String modelParams;
    
    /**
     * Status (enabled-disabled)
     */
    @TableField("STATUS")
    private String status;

    /**
     * Response limit 0 - 8192
     */
    @TableField("RESPONSE_LIMIT")
    private Integer responseLimit;

    /**
     * Randomness
     */
    @TableField("RANDOM_INDEX")
    private Integer randomIndex;

    /**
     * Top probability
     */
    @TableField("TOP_VARIANCE")
    private Integer topVariance;
    
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


    private Long maxQuotaTokens;
}