package com.astrsomn.api.storage.entity;

import com.astrsomn.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
@TableName("AST_FILE_RECORD")
public class AstFileRecordEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("BIZ_TYPE")
    private String bizType;

    @TableField("BIZ_ID")
    private String bizId;

    @TableField("PLATFORM")
    private String platform;

    @TableField("BUCKET")
    private String bucket;

    @TableField("OBJECT_KEY")
    private String objectKey;

    @TableField("ORIGIN_NAME")
    private String originName;

    @TableField("MIME_TYPE")
    private String mimeType;

    @TableField("FILE_SIZE")
    private Long fileSize;

    @TableField("ETAG")
    private String etag;

    @TableField("STATUS")
    private String status;

    @TableField("FILE_URL")
    private String fileUrl;
}