package com.astrsomn.api.workflow.domain.dto.timerjob;

import com.astrsomn.common.base.BaseEntity;
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
