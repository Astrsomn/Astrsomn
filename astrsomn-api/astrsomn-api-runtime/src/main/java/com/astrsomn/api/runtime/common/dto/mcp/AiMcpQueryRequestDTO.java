package com.astrsomn.api.runtime.common.dto.mcp;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.AiMcpEntity;

import java.io.Serializable;

@Data
public class AiMcpQueryRequestDTO extends AiMcpEntity implements Serializable {

    private String mcpKey;
    private String type;
    private Integer enabled;

}
