package org.astrsomn.core.common.dto.model;

import lombok.Data;
import org.astrsomn.core.common.entity.AiModelEntity;

@Data
public class AiModelResponseDTO extends AiModelEntity {

    /**
     * 是否有推理实例在同环境下引用该 modelKey；为 true 时前端应禁止修改模型 Key。
     */
    private Boolean modelKeyImmutable;
}
