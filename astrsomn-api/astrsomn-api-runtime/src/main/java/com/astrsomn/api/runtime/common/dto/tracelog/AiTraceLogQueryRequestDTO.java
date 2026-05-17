package com.astrsomn.api.runtime.common.dto.tracelog;

import com.astrsomn.api.runtime.common.entity.AiTraceLogEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AiTraceLogQueryRequestDTO extends AiTraceLogEntity implements Serializable {

    private String traceId;
    private Long conversationId;
    private Long agentId;
    private Long instanceId;
    private String nodeType;
    private String status;
}