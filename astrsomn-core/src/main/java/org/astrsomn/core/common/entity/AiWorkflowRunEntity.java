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
 * 工作流运行实例（审计 / 恢复 / 与对话 MEMORY_KEY 对齐）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("AI_WORKFLOW_RUN")
public class AiWorkflowRunEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("EXECUTION_ID")
    private String executionId;

    @TableField("WORKFLOW_KEY")
    private String workflowKey;

    @TableField("VERSION_NO")
    private Integer versionNo;

    @TableField("STATUS")
    private String status;

    @TableField("LAST_NODE_ID")
    private String lastNodeId;

    @TableField("CONTEXT_JSON")
    private String contextJson;

    @TableField("AGENT_KEY")
    private String agentKey;

    @TableField("MEMORY_KEY")
    private String memoryKey;

    @TableField("ERROR_MESSAGE")
    private String errorMessage;
}
