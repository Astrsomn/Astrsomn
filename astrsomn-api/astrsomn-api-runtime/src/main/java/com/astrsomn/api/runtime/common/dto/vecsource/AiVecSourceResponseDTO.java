package com.astrsomn.api.runtime.common.dto.vecsource;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;

@Data
public class AiVecSourceResponseDTO extends AiVecSourceEntity {

    /**
     * 扩展库头像（完整 data URL）
     */
    private String providerAvatar;

    /**
     * 扩展名称（用于前端展示）
     */
    private String extensionName;

    /**
     * 扩展类型（用于前端展示）
     */
    private String extensionType;
}
