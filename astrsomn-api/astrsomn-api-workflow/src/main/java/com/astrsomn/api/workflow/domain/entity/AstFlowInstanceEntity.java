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
@TableName("AST_FLOW_INSTANCE")
public class AstFlowInstanceEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("DEPLOYMENT_ID")
    private Long deploymentId;

    @TableField("BUSINESS_KEY")
    private String businessKey;

    @TableField("EXECUTION_STATUS")
    private String executionStatus;

    @TableField("CURRENT_NODE_ID")
    private String currentNodeId;

    @TableField("STATE_JSON")
    private String stateJson;
}