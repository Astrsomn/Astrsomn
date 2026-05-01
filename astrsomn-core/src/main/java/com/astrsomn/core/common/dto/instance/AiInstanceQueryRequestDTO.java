package com.astrsomn.core.common.dto.instance;

import com.astrsomn.core.common.constant.AiModelEnum;
import lombok.Data;
import com.astrsomn.core.common.entity.AiInstanceEntity;

import java.io.Serializable;

@Data
public class AiInstanceQueryRequestDTO extends AiInstanceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 非表字段：按关联 {@code AI_MODEL.MODEL_TYPE} 筛选，取值与 {@link AiModelEnum.ModelTypeEnum} 一致：chat / embedding / image。
     */
    private String modelType;

    /**
     * 非表字段：按关联 {@code AI_MODEL.EXTENSION_CODE}（模型提供商）筛选。
     */
    private String extensionCode;
}
