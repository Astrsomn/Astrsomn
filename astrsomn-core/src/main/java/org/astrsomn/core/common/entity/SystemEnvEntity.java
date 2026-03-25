package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;



@Data
@TableName("SYSTEM_ENV")
public class SystemEnvEntity extends BaseEntity<Long> {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Environment name
     */
    @TableField(value = "ENV_NAME")
    private String envName;

    /**
     * Environment code DEV SIT UAT PRO
     */
    @TableField(value = "ENV_KEY")
    private String envKey;

    /**
     * Description
     */
    @TableField(value = "DESCRIPTION")
    private String description;


}
