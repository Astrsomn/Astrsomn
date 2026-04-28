package com.astrsomn.workflow.core.domain.entity;

import com.astrsomn.commn.base.BaseEntity;
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
@TableName("AST_FLOW_MSG_OUTBOX")
public class AstFlowMsgOutboxEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("BIZ_TYPE")
    private String bizType;

    @TableField("BIZ_ID")
    private String bizId;

    @TableField("TOPIC_OR_ENDPOINT")
    private String topicOrEndpoint;

    @TableField("PAYLOAD_JSON")
    private String payloadJson;

    @TableField("MSG_STATUS")
    private String msgStatus;

    @TableField("RETRY_COUNT")
    private Integer retryCount;

    @TableField("NEXT_RETRY_TIME_MS")
    private Long nextRetryTimeMs;

    @TableField("LAST_ERROR")
    private String lastError;

    @TableField("IDEMPOTENT_KEY")
    private String idempotentKey;
}
