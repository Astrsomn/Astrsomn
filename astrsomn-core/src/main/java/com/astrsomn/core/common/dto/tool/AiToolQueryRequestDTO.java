package com.astrsomn.core.common.dto.tool;

import lombok.Data;
import com.astrsomn.core.common.entity.AiToolEntity;

import java.io.Serializable;

@Data
public class AiToolQueryRequestDTO extends AiToolEntity implements Serializable {

    private String toolKey;
    private String toolName;
    private String type;
    private String enableFlag;

}
