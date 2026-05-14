package com.astrsomn.api.runtime.common.entity;

import com.astrsomn.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
@TableName("SYS_EXTENSION")
public class SystemExtensionEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("EXTENSION_KEY")
    private String extensionKey;

    @TableField("EXTENSION_NAME")
    private String extensionName;

    @TableField("TYPE")
    private String type;

    @TableField("VERSION")
    private String version;

    @TableField("AUTHOR")
    private String author;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("JAR_NAME")
    private String jarName;

    @TableField("APPLIED")
    private String applied;

    @TableField("STATUS")
    private String status;

    @TableField("EXTENSION_CODE")
    private String extensionCode;

    @TableField("AVATAR")
    private String avatar;

    @TableField("CHANGELOG")
    private String changelog;

    @TableField("MIN_SERVER_VERSION")
    private String minServerVersion;

    @TableField("DISCOVERY_MECHANISM")
    private String discoveryMechanism;

    @TableField("INSTALL_SOURCE")
    private String installSource;

}