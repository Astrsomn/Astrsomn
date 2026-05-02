package com.astrsomn.api.runtime.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import com.astrsomn.common.base.BaseEntity;

/**
 * AI template engine entity (supports .ftl / .st two formats)
 */
@Data
@TableName("AI_TEMPLATE")
public class AiTemplateEntity extends BaseEntity<Long> {


    /**
     * id
     */
    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * Key shared by multiple versions of the same template
     */
    @TableField("TEMPLATE_KEY")
    private String templateKey;


    /**
     * Name
     */
    @TableField("TEMPLATE_TITLE")
    private String templateTitle;

    /**
     * Template content
     */
    @TableField("CONTENT")
    private String content;

    /**
     * Category
     */
    @TableField("CATEGORY")
    private String category;

    /**
     * Template type: FREEMARKER(.ftl), STRING_TEMPLATE(.st)
     */
    @TableField("TEMPLATE_TYPE")
    private String templateType;

    /**
     * Version number
     */
    @TableField("VERSION")
    private Integer version;

    /**
     * Enabled flag
     */
    @TableField("STATUS")
    private String status;


}
