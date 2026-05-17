package com.astrsomn.api.runtime.common.dto.template;

import com.astrsomn.api.runtime.common.entity.AiTemplateEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AiTemplateQueryRequestDTO extends AiTemplateEntity implements Serializable {

    private String templateKey;
    private String templateTitle;
    private String category;
    private String templateType;
    private String status;

}