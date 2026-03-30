package org.astrsomn.core.common.dto.template;

import lombok.Data;
import org.astrsomn.core.common.entity.AiTemplateEntity;

import java.io.Serializable;

@Data
public class AiTemplateQueryRequestDTO extends AiTemplateEntity implements Serializable {

    private String templateKey;
    private String templateTitle;
    private String category;
    private String templateType;
    private String status;

}
