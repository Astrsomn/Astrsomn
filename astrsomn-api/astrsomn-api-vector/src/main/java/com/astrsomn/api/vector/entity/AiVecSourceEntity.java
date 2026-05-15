package com.astrsomn.api.vector.entity;

import com.astrsomn.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
@TableName("AI_VEC_SOURCE")
public class AiVecSourceEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("NAME")
    private String name;

    @TableField("EXTENSION_CODE")
    private String extensionCode;


    @TableField("HOST")
    private String host;

    @TableField("PORT")
    private String port;

    @TableField("USERNAME")
    private String username;

    @TableField("PASSWORD")
    private String password;

    @TableField("DATABASE_NAME")
    private String databaseName;


    @TableField("TOKEN")
    private String token;

    @TableField("CONFIG_JSON")
    private String configJson;

    @TableField("STATUS")
    private String status;
}