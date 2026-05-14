package com.astrsomn.api.workflow.domain.dto.humantask;

import com.astrsomn.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AstFlowHumanTaskQueryRequestDTO extends BaseEntity<Long> {

    private Long instanceId;
    private String nodeId;
    private String taskStatus;
}