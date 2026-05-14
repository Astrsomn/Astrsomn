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
@TableName("AST_FLOW_NODE_CONFIG")
public class AstFlowNodeConfigEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("FLOW_DEFINITION_ID")
    private Long flowDefinitionId;

    @TableField("NODE_ID")
    private String nodeId;

    @TableField("NODE_TYPE")
    private String nodeType;

    @TableField("CONFIG_JSON")
    private String configJson;
}