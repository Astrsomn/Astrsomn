package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;

/**
 * AI 模板引擎实体（支持 .ftl / .st 两种格式）
 */
@Data
@TableName("AI_TEMPLATE")
public class AiTemplateEntity extends BaseEntity<Long> {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 同一模板多版本共用的 UUID
     */
    @TableField("template_uuid")
    private String templateUuid;

    /**
     * 唯一编码
     */
    @TableField("CODE")
    private String code;

    /**
     * 名称
     */
    @TableField("NAME")
    private String name;


    /**
     * 分类
     */
    @TableField("CATEGORY")
    private String category;

    /**
     * 模板类型：FREEMARKER(.ftl)、STRING_TEMPLATE(.st)
     */
    @TableField("TEMPLATE_TYPE")
    private String templateType;

    /**
     * 版本号
     */
    @TableField("VERSION")
    private Integer version;

    /**
     * 是否启用
     */
    @TableField("ENABLE_FLAG")
    private String enabledFlag;


    /**
     * 模板内容
     */
    @TableField("CONTENT")
    private String content;
}
