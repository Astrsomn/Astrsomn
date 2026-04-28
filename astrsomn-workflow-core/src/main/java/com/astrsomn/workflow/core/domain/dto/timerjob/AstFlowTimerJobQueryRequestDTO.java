package com.astrsomn.workflow.core.domain.dto.timerjob;

import com.astrsomn.commn.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowTimerJobQueryRequestDTO extends BaseEntity<Long> {

    private Long instanceId;
    private String nodeId;
    private String jobType;
    private String jobStatus;
}
