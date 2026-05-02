package com.astrsomn.api.runtime.common.dto.instance;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;

@Data
public class AiInstanceResponseDTO extends AiInstanceEntity {

    /**
     * 关联模型 {@code AI_MODEL.PROVIDER} 在 {@code SYSTEM_EXTENSION} 中匹配到的展示头像（SVG 等）。
     */
    private String providerAvatar;
}
