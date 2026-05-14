package com.astrsomn.api.runtime.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.astrsomn.common.base.BaseEntity;

import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@TableName("AI_MODEL")
public class AiModelEntity extends BaseEntity<Long> {
    
    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    
    @TableField("MODEL_KEY")
    private String modelKey;

    
    @TableField("MODEL_NAME")
    private String modelName;

    
    @TableField("DESCRIPTION")
    private String description;

    
    @TableField("MODEL_TYPE")
    private String modelType;

    
    @TableField("EXTENSION_CODE")
    private String extensionCode;

    
    @TableField("STATUS")
    private String status;


    
    @TableField("IS_DEFAULT")
    private String isDefault;
    
    
    @TableField("CAPABILITIES")
    private String capabilities;

    
    @TableField("PARAMS")
    private String params;
    
    @TableField("MAX_QUOTA_TOKENS")
    private Long maxQuotaTokens;


    @TableField("SOURCE_TYPE")
    private String sourceType;


}