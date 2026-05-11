package com.astrsomn.api.runtime.common.dto.agent;


import lombok.Data;
import com.astrsomn.api.runtime.common.entity.AiAgentEntity;

import java.io.Serializable;

@Data
public class AiAgentQueryRequestDTO extends AiAgentEntity implements Serializable {

    private String name;
    private String status;

    /** 非表字段：按关联 AI_MODEL.EXTENSION_CODE（模型提供商）筛选 */
    private String extensionCode;

}
