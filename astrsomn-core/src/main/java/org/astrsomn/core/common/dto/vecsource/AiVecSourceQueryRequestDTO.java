package org.astrsomn.core.common.dto.vecsource;

import lombok.Data;
import org.astrsomn.core.common.entity.AiVecSourceEntity;

import java.io.Serializable;

@Data
public class AiVecSourceQueryRequestDTO extends AiVecSourceEntity implements Serializable {

    private String name;
    private String provider;
    private String status;
}
