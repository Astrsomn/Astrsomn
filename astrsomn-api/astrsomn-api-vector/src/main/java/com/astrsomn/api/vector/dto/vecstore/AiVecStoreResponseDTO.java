package com.astrsomn.api.vector.dto.vecstore;

import com.astrsomn.api.runtime.common.entity.AiVecStoreEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class AiVecStoreResponseDTO extends AiVecStoreEntity {

    @TableField(exist = false)
    private String sourceName;

    @TableField(exist = false)
    private String sourceProvider;

    @TableField(exist = false)
    private String instanceName;
}