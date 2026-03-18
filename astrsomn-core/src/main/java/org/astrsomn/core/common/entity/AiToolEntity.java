package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;

@Data
@TableName("AI_TOOL")
public class AiToolEntity extends BaseEntity<Long> {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Tool name
     */
    @TableField("TOOL_NAME")
    private String toolName;

    /**
     * Tool Key
     */
    @TableField("TOOL_KEY")
    private String toolKey;
    /**
     * Tool description
     */
    @TableField("DESCRIPTION")
    private String description;

    /**
     * Bean name
     */
    @TableField("BEAN_NAME")
    private String beanName;

    /**
     * Method name
     */
    @TableField("METHOD_NAME")
    private String methodName;

    /**
     * Tool type (html page, call method, api)
     */
    @TableField("TYPE")
    private String type;

    /**
     * Status (enabled/disabled)
     */
    @TableField("ENABLE_FLAG")
    private String enableFlag;

}
