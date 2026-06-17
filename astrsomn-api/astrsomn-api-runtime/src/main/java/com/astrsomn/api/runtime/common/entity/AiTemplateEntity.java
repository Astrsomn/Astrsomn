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
@TableName("AI_TEMPLATE")
public class AiTemplateEntity extends BaseEntity<Long> {


    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    @TableField("TEMPLATE_KEY")
    private String templateKey;


    @TableField("TEMPLATE_TITLE")
    private String templateTitle;


    @TableField("CONTENT")
    private String content;


    @TableField("CATEGORY")
    private String category;


    @TableField("TEMPLATE_TYPE")
    private String templateType;


    @TableField("PARAMS_DEFINITION")
    private String paramsDefinition;


    @TableField("VERSION")
    private Integer version;


    @TableField("STATUS")
    private String status;


}