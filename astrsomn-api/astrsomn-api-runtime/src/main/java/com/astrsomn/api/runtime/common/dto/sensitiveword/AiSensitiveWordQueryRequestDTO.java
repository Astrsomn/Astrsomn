package com.astrsomn.api.runtime.common.dto.sensitiveword;

import com.astrsomn.api.runtime.common.entity.AiSensitiveWordEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AiSensitiveWordQueryRequestDTO extends AiSensitiveWordEntity implements Serializable {

    private String word;
    private String matchType;
    private String scopeKey;
    private String action;
    private String status;
    private String category;
}