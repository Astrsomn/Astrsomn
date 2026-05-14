package com.astrsomn.api.runtime.common.dto.template;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.AiTemplateEntity;

import java.io.Serializable;

@Data
public class AiTemplateQueryRequestDTO extends AiTemplateEntity implements Serializable {

    private String templateKey;
    private String templateTitle;
    private String category;
    private String templateType;
    private String status;

}