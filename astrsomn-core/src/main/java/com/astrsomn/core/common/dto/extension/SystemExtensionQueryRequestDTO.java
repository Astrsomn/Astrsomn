package com.astrsomn.core.common.dto.extension;

import lombok.Data;
import com.astrsomn.core.common.entity.SystemExtensionEntity;

import java.io.Serializable;

@Data
public class SystemExtensionQueryRequestDTO extends SystemExtensionEntity implements Serializable {

    private String extensionKey;
    private String extensionName;
    private String type;
    private String status;
    private String listScope;
}
