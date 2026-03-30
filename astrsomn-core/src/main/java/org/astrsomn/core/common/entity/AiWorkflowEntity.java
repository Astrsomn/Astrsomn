package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.astrsomn.core.common.base.BaseEntity;

/**
 * 工作流定义（图 JSON 与 VueFlow 导出对齐，由引擎反序列化为 {@code org.astrsomn.workflow.core.model.WorkflowDefinition}）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("AI_WORKFLOW")
public class AiWorkflowEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("WORKFLOW_KEY")
    private String workflowKey;

    @TableField("WORKFLOW_NAME")
    private String workflowName;

    @TableField("VERSION_NO")
    private Integer versionNo;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("GRAPH_JSON")
    private String graphJson;

    @TableField("STATUS")
    private String status;
}
