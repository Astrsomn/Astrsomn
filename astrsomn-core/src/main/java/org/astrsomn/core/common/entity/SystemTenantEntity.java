package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;

@Data
@TableName("SYSTEM_TENANT")
public class SystemTenantEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 租户名称
     */
    @TableField(value = "TENANT_NAME")
    private String tenantName;

    /**
     * 描述
     */
    @TableField("DESCRIPTION")
    private String description;


}
