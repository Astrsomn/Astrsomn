package org.astrsomn.core.common.dto.config;

import lombok.Data;
import org.astrsomn.core.common.entity.SystemConfigEntity;

import java.io.Serializable;

@Data
public class SystemConfigQueryRequestDTO extends SystemConfigEntity implements Serializable {

    private String configKey;
    private String configGroup;
    private String status;
}
