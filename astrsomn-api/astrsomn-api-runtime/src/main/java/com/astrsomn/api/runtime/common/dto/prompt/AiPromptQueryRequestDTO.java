package com.astrsomn.api.runtime.common.dto.prompt;

import com.astrsomn.api.runtime.common.entity.AiPromptEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AiPromptQueryRequestDTO extends AiPromptEntity implements Serializable {

    private String promptKey;
    private String promptTitle;
    private String scene;


}