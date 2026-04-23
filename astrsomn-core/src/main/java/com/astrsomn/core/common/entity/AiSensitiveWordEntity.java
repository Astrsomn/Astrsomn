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
@TableName("AI_SENSITIVE_WORD")
@EqualsAndHashCode(callSuper = true)
public class AiSensitiveWordEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 敏感词内容（或正则表达式）
     */
    @TableField("WORD")
    private String word;

    /**
     * 匹配类型：EXACT(精确), FUZZY(模糊), REGEX(正则)
     */
    @TableField("MATCH_TYPE")
    private String matchType;

    /**
     * 作用范围：ALL(全局), 指定 AGENT_KEY (特定智能体)
     */
    @TableField("SCOPE_KEY")
    private String scopeKey;

    /**
     * 处置动作：BLOCK(直接拦截报错), REPLACE(替换为*), WARN(仅记录日志)
     */
    @TableField("ACTION")
    private String action;

    /**
     * 替换文本（当 ACTION 为 REPLACE 时使用）
     */
    @TableField("REPLACEMENT")
    private String replacement;

    /**
     * 状态：ENABLED, DISABLED
     */
    @TableField("STATUS")
    private String status;

    /**
     * 标签/分类：POLITICS, VIOLENCE, ADULT, CUSTOM
     */
    @TableField("CATEGORY")
    private String category;
}
