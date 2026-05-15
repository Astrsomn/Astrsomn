package com.astrsomn.system.dto.config;

import com.astrsomn.system.entity.SystemConfigEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class SystemConfigQueryRequestDTO extends SystemConfigEntity implements Serializable {

    private String configKey;
    private String configGroup;
    private String status;
}