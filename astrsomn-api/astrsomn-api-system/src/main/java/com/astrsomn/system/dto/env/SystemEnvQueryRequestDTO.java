package com.astrsomn.system.dto.env;

import com.astrsomn.system.entity.SystemEnvEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class SystemEnvQueryRequestDTO extends SystemEnvEntity implements Serializable {

    private String envName;
    private String envCode;

}