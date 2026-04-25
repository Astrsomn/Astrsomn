package com.astrsomn.workflow.core.domain.dto.instance;

import com.astrsomn.commn.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowInstanceQueryRequestDTO extends BaseEntity<Long> {

    private Long deploymentId;
    private String businessKey;
    private String executionStatus;
    private String currentNodeId;
}
