package com.astrsomn.api.runtime.common.dto.vecsource;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;

import java.io.Serializable;

@Data
public class AiVecSourceQueryRequestDTO extends AiVecSourceEntity implements Serializable {

    private String name;

    private String status;
}
