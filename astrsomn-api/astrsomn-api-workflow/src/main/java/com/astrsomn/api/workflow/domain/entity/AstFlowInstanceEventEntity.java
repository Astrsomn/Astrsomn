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
@TableName("AST_FLOW_INSTANCE_EVENT")
public class AstFlowInstanceEventEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("INSTANCE_ID")
    private Long instanceId;

    @TableField("EVENT_TYPE")
    private String eventType;

    @TableField("NODE_ID")
    private String nodeId;

    @TableField("EVENT_TIME_MS")
    private Long eventTimeMs;

    @TableField("EVENT_DATA_JSON")
    private String eventDataJson;

    @TableField("TRACE_ID")
    private String traceId;
}