package com.astrsomn.core.common.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.astrsomn.commn.base.BaseEntity;

@Data
@TableName("SYS_CONFIG")
@EqualsAndHashCode(callSuper = true)
public class SystemConfigEntity extends BaseEntity<Long>{
    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 配置项 Key (例如: email.smtp.host, email.smtp.port)
     */
    @TableField("CONFIG_KEY")
    private String configKey;

    /**
     * 配置项内容 (如果是密码，建议在 Service 层进行加密存储)
     */
    @TableField("CONFIG_VALUE")
    private String configValue;

    /**
     * 配置分组：EMAIL, STORAGE, LLM_GATEWAY
     */
    @TableField("CONFIG_GROUP")
    private String configGroup;

    /**
     * 配置描述
     */
    @TableField("DESCRIPTION")
    private String description;

    /**
     * 是否为系统内置（内置配置通常不允许删除）
     */
    @TableField("IS_SYSTEM")
    private Boolean isSystem;

    /**
     * 状态：ENABLED, DISABLED
     */
    @TableField("STATUS")
    private String status;
}
