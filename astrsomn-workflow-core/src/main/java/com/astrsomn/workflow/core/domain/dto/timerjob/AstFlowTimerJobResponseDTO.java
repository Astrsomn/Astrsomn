package com.astrsomn.workflow.core.domain.dto.timerjob;

import com.astrsomn.commn.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowTimerJobResponseDTO extends BaseEntity<Long> {

    private Long instanceId;
    private String nodeId;
    private String jobType;
    private Long dueTimeMs;
    private String jobStatus;
    private Integer retryCount;
    private Integer maxRetry;
    private String lastError;
    private String payloadJson;
}
