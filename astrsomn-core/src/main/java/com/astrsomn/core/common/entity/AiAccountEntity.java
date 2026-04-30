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
@EqualsAndHashCode(callSuper = true)
@TableName("AI_ACCOUNT")
public class AiAccountEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("ACCOUNT_KEY")
    private String accountKey;


    @TableField("ACCOUNT_NAME")
    private String accountName;

    @TableField("API_KEY")
    private String apiKey;

    @TableField("API_SECRET")
    private String apiSecret;

    @TableField("ACCOUNT_TOKENS")
    private Long accountTokens;

    @TableField("API_URL")
    private String apiUrl;

    @TableField("EXTENSION_CODE")
    private String extensionCode;

    @TableField("STATUS")
    private String status;
}
