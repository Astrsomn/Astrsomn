package org.astrsomn.core.common.dto.tracelog;

import lombok.Data;
import org.astrsomn.core.common.entity.AiTraceLogEntity;

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
