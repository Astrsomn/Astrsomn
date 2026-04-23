package com.astrsomn.core.common.dto.mcp;

import lombok.Data;
import com.astrsomn.core.common.entity.AiMcpEntity;

import java.io.Serializable;

@Data
public class AiMcpQueryRequestDTO extends AiMcpEntity implements Serializable {

    private String mcpKey;
    private String type;
    private Integer enabled;

}
