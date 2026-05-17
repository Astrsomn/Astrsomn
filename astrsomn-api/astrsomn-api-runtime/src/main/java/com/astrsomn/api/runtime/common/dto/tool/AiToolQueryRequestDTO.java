package com.astrsomn.api.runtime.common.dto.tool;

import com.astrsomn.api.runtime.common.entity.AiToolEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AiToolQueryRequestDTO extends AiToolEntity implements Serializable {

    private String toolKey;
    private String toolName;
    private String type;
    private String enableFlag;

}