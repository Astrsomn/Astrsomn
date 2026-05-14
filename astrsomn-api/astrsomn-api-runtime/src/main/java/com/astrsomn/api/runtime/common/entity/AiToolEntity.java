package com.astrsomn.api.runtime.common.entity;

import com.astrsomn.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
@TableName("AI_TOOL")
public class AiToolEntity extends BaseEntity<Long> {


    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    @TableField("TOOL_NAME")
    private String toolName;


    @TableField("TOOL_KEY")
    private String toolKey;

    @TableField("DESCRIPTION")
    private String description;


    @TableField("BEAN_NAME")
    private String beanName;


    @TableField("METHOD_NAME")
    private String methodName;


    @TableField("TYPE")
    private String type;


    @TableField("STATUS")
    private String status;


    @TableField("CLASS_NAME")
    private String className;
}