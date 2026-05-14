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
@EqualsAndHashCode(callSuper = true)
@TableName("SYS_MESSAGE")
public class SystemMessageEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    @TableField("MESSAGE_TYPE")
    private String messageType;


    @TableField("MESSAGE_LEVEL")
    private String messageLevel;


    @TableField("READ_STATUS")
    private String readStatus;


    @TableField("TITLE")
    private String title;


    @TableField("CONTENT")
    private String content;


    @TableField("REF_TYPE")
    private String refType;


    @TableField("REF_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long refId;


    @TableField("REF_KEY")
    private String refKey;


    @TableField("SOURCE")
    private String source;


    @TableField("ERROR_CODE")
    private String errorCode;
}