package com.astrsomn.api.workflow.domain.entity;

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
@EqualsAndHashCode(callSuper = false)
@TableName("AST_FLOW_BIZ_IDEMPOTENT")
public class AstFlowBizIdempotentEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("IDEMPOTENT_KEY")
    private String idempotentKey;

    @TableField("BIZ_TYPE")
    private String bizType;

    @TableField("BIZ_ID")
    private String bizId;

    @TableField("REQUEST_HASH")
    private String requestHash;

    @TableField("RESULT_REF")
    private String resultRef;

    @TableField("EXPIRE_AT_MS")
    private Long expireAtMs;
}