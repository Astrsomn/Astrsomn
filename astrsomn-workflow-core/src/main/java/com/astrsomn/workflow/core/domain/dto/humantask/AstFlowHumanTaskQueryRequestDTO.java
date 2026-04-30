package com.astrsomn.workflow.core.domain.dto.humantask;

import com.astrsomn.commn.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowHumanTaskQueryRequestDTO extends BaseEntity<Long> {

    private Long instanceId;
    private String nodeId;
    private String taskStatus;
}
