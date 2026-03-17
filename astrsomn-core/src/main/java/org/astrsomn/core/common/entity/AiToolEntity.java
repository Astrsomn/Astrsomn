package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;

@Data
@TableName("AI_TOOL")
public class AiToolEntity extends BaseEntity<Long> {

    /**
     * 工具名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 工具描述
     */
    @TableField("DESCRIPTION")
    private String description;


    /**
     * bean名称
     */
    @TableField("BEAN_NAME")
    private String beanName;

    /**
     * 方法名
     */
    @TableField("METHOD_NAME")
    private String methodName;

    /**
     * 工具类型（html页面，调用方法，api）
     */
    @TableField("TYPE")
    private String type;

    /**
     * 状态（启用/禁用）
     */
    @TableField("ENABLE_FLAG")
    private String enableFlag;

}
