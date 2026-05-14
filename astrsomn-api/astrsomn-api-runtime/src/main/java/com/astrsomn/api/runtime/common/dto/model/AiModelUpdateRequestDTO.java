package com.astrsomn.api.runtime.common.dto.model;

import com.astrsomn.common.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;


@Data
public class AiModelUpdateRequestDTO extends AiModelCreateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;
}