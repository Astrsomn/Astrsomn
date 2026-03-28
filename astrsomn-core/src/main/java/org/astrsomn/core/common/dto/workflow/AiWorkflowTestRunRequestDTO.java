package org.astrsomn.core.common.dto.workflow;

import lombok.Data;

@Data
public class AiWorkflowTestRunRequestDTO {

    /** 与 workflowKey 二选一：按主键执行当前保存的图（含草稿） */
    private Long id;

    /** 与 id 二选一：按已发布版本执行（STATUS=PUBLISHED 中版本号最大的一条） */
    private String workflowKey;

    private String userMessage;

    private String memoryKey;
}
