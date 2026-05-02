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
@TableName("AST_FLOW_TIMER_JOB")
public class AstFlowTimerJobEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("INSTANCE_ID")
    private Long instanceId;

    @TableField("NODE_ID")
    private String nodeId;

    @TableField("JOB_TYPE")
    private String jobType;

    @TableField("DUE_TIME_MS")
    private Long dueTimeMs;

    @TableField("JOB_STATUS")
    private String jobStatus;

    @TableField("RETRY_COUNT")
    private Integer retryCount;

    @TableField("MAX_RETRY")
    private Integer maxRetry;

    @TableField("LAST_ERROR")
    private String lastError;

    @TableField("PAYLOAD_JSON")
    private String payloadJson;
}
