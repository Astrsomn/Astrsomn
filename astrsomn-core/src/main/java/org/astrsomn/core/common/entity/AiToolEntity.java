package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;

@Data
@TableName("AI_TOOL")
public class AiToolEntity extends BaseEntity<Long> {

    /**
     * 工具名称
     */
    private String name;

    /**
     * 工具描述
     */
    private String description;


    /**
     * 类名
     */
    private String beanName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 工具类型（html页面，调用方法，api）
     */
    private String type;

    /**
     * 状态（启用/禁用）
     */
    private String status;

}
