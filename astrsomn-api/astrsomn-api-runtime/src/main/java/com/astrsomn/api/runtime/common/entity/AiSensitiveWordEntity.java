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
@TableName("AI_SENSITIVE_WORD")
@EqualsAndHashCode(callSuper = true)
public class AiSensitiveWordEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    
    @TableField("WORD")
    private String word;

    
    @TableField("MATCH_TYPE")
    private String matchType;

    
    @TableField("SCOPE_KEY")
    private String scopeKey;

    
    @TableField("ACTION")
    private String action;

    
    @TableField("REPLACEMENT")
    private String replacement;

    
    @TableField("STATUS")
    private String status;

    
    @TableField("CATEGORY")
    private String category;
}