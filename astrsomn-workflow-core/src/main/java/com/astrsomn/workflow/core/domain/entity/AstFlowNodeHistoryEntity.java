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
@TableName("AST_FLOW_NODE_HISTORY")
public class AstFlowNodeHistoryEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("INSTANCE_ID")
    private Long instanceId;

    @TableField("FLOW_DEFINITION_ID")
    private Long flowDefinitionId;

    @TableField("VERSION")
    private Integer version;

    @TableField("HISTORY_TYPE")
    private String historyType;

    @TableField("NODE_ID")
    private String nodeId;

    @TableField("NODE_NAME")
    private String nodeName;

    @TableField("INPUT_DATA")
    private String inputData;

    @TableField("OUTPUT_DATA")
    private String outputData;

    @TableField("SNAPSHOT_JSON")
    private String snapshotJson;

    @TableField("EXECUTION_MS")
    private Long executionMs;
}
