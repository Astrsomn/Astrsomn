package org.astrsomn.core.common.dto.instance;

import lombok.Data;
import org.astrsomn.core.common.entity.AiInstanceEntity;

import java.io.Serializable;

@Data
public class AiInstanceQueryRequestDTO extends AiInstanceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 非表字段：按关联 {@code AI_MODEL.MODEL_TYPE} 筛选，取值与 {@link org.astrsomn.core.common.constant.AiModelEnum.ModelTypeEnum} 一致：chat / embedding / image。
     */
    private String modelType;
}
