package com.astrsomn.core.common.dto.prompt;

import lombok.Data;
import com.astrsomn.core.common.entity.AiPromptEntity;

import java.io.Serializable;

@Data
public class AiPromptQueryRequestDTO extends AiPromptEntity implements Serializable {

    private String promptKey;
    private String promptTitle;
    private String scene;


}
