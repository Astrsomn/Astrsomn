package com.astrsomn.core.common.dto.env;

import lombok.Data;
import com.astrsomn.core.common.entity.SystemEnvEntity;

import java.io.Serializable;

@Data
public class SystemEnvQueryRequestDTO extends SystemEnvEntity implements Serializable {

    private String envName;
    private String envCode;

}
