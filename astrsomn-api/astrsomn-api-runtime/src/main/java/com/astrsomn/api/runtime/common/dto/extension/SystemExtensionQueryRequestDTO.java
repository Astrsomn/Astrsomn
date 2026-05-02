package com.astrsomn.api.runtime.common.dto.extension;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.SystemExtensionEntity;

import java.io.Serializable;

@Data
public class SystemExtensionQueryRequestDTO extends SystemExtensionEntity implements Serializable {

    private String extensionKey;
    private String extensionName;
    private String type;
    private String status;
    private String listScope;
}
