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
@TableName("AST_FLOW_HUMAN_TASK")
public class AstFlowHumanTaskEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("INSTANCE_ID")
    private Long instanceId;

    @TableField("NODE_ID")
    private String nodeId;

    @TableField("TASK_STATUS")
    private String taskStatus;

    @TableField("PAYLOAD")
    private String payload;

    @TableField("ACTION_DATA")
    private String actionData;
}
