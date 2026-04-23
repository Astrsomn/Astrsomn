package com.astrsomn.core.common.dto.model;


import lombok.Data;
import com.astrsomn.core.common.entity.AiModelEntity;

import java.io.Serializable;


@Data
public class AiModelQueryRequestDTO extends AiModelEntity implements Serializable {

    /**
     * 供应商
     */
    private String supplier;
}
