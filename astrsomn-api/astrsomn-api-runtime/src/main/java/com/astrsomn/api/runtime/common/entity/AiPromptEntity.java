package com.astrsomn.api.runtime.common.entity;

import com.astrsomn.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("AI_PROMPT")
public class AiPromptEntity extends BaseEntity<Long> {


    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    @TableField("PROMPT_KEY")
    private String promptKey;


    @TableField("PROMPT_TITLE")
    private String promptTitle;


    @TableField("PROMPT_CONTENT")
    private String promptContent;


    @TableField("SCENE")
    private String scene;


    @TableField("STATUS")
    private String status;


    @TableField("VERSION")
    private Integer version;
}