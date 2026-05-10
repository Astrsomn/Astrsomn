package com.astrsomn.api.runtime.common.dto.instance;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;

@Data
public class AiInstanceResponseDTO extends AiInstanceEntity {

    /**
     * 关联模型 {@code AI_MODEL.PROVIDER} 在 {@code SYSTEM_EXTENSION} 中匹配到的展示头像（SVG 等）。
     */
    private String providerAvatar;

    /**
     * 关联账号名称，通过 ACCOUNT_KEY 关联查询 AI_ACCOUNT 表获取。
     */
    private String accountName;
}
