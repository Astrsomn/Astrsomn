package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.astrsomn.core.common.base.BaseEntity;

/**
 * AI 模板引擎实体（支持 .ftl / .st 两种格式）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("AI_TEMPLATE_ENGINE")
public class AiTemplateEngineEntity extends BaseEntity<Long> {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 同一模板多版本共用的 UUID */
    @TableField("template_uuid")
    private String templateUuid;

    /** 编码，如 code-review-user.ftl、knowledge-user.st */
    private String code;

    /** 名称 */
    private String name;

    /** 分类：RAG、CODE_REVIEW、OTHER 等 */
    private String category;

    /** 模板类型：FREEMARKER(.ftl)、STRING_TEMPLATE(.st) */
    @TableField("template_type")
    private String templateType;

    /** 版本号 */
    private Integer version;

    /** 是否启用：1-是 0-否 */
    private Integer enabled;

    /** 是否系统内置：1-是 0-否 */
    @TableField("system_builtin")
    private Integer systemBuiltin;

    /** 模板内容 */
    private String content;
}
