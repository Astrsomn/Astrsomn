package com.astrsomn.api.runtime.common.dto.config;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.SystemConfigEntity;

import java.io.Serializable;

@Data
public class SystemConfigQueryRequestDTO extends SystemConfigEntity implements Serializable {

    private String configKey;
    private String configGroup;
    private String status;
}
