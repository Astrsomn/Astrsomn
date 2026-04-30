package com.astrsomn.core.common.dto.instance;

import lombok.Data;
import com.astrsomn.core.common.entity.AiInstanceEntity;

import java.io.Serializable;

@Data
public class AiInstanceCreateRequestDTO extends AiInstanceEntity implements Serializable {

    private static final long serialVersionUID = 1L;
}
