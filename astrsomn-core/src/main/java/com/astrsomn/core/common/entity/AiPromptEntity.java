package com.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.astrsomn.core.common.base.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("AI_PROMPT")
public class AiPromptEntity extends BaseEntity<Long> {


    /**
     * id
     */
    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * UUID - multiple versions of the prompt share the same UUID to ensure history can be restored
     */
    @TableField("PROMPT_KEY")
    private String promptKey;

    /**
     * Title
     */
    @TableField("PROMPT_TITLE")
    private String promptTitle;

    /**
     * Content
     */
    @TableField("PROMPT_CONTENT")
    private String promptContent;

    /**
     * Scene classification
     */
    @TableField("SCENE")
    private String scene;

    /**
     * Status - enable/disable
     */
    @TableField("STATUS")
    private String status;

    /**
     * Version number
     */
    @TableField("VERSION")
    private Integer version;
}