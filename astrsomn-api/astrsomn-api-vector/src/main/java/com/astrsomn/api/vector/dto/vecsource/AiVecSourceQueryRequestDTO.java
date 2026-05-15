package com.astrsomn.api.vector.dto.vecsource;

import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AiVecSourceQueryRequestDTO extends AiVecSourceEntity implements Serializable {

    private String name;

    private String status;
}