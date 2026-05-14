package com.astrsomn.api.runtime.common.entity;

import com.astrsomn.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("SYS_CONFIG")
@EqualsAndHashCode(callSuper = true)
public class SystemConfigEntity extends BaseEntity<Long> {
    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    @TableField("CONFIG_KEY")
    private String configKey;


    @TableField("CONFIG_VALUE")
    private String configValue;


    @TableField("CONFIG_GROUP")
    private String configGroup;


    @TableField("DESCRIPTION")
    private String description;


    @TableField("IS_SYSTEM")
    private Boolean isSystem;


    @TableField("STATUS")
    private String status;
}