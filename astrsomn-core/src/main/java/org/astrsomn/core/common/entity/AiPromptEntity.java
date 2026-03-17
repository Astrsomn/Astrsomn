package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.astrsomn.core.common.base.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("AI_PROMPT")
public class AiPromptEntity extends BaseEntity<Long> {


    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * UUID 提示词的多个版本共用一个UUID，确保能复原历史
     */
    @TableField("prompt_uuid")
    private String promptUUID;

    /**
     * 标题
     */
    @TableField("PROMPT_TITLE")
    private String promptTitle;

    /**
     * 内容
     */
    @TableField("PROMPT_CONTENT")
    private String promptContent;

    /**
     * 场景分类
     */
    @TableField("SCENE")
    private String scene;

    /**
     * 状态  启用enable/禁用disable
     */
    @TableField("ENABLE_FLAG")
    private String enabledFlag;

    /**
     * 版本号
     */
    @TableField("VERSION")
    private Integer version;
}