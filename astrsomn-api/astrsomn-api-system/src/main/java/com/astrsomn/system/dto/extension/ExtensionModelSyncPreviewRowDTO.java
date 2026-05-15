package com.astrsomn.system.dto.extension;

import lombok.Data;


@Data
public class ExtensionModelSyncPreviewRowDTO {
    private String modelKey;
    private String modelName;
    private String modelType;
    private String provider;
}