package com.astrsomn.core.common.dto.extension;

import lombok.Data;

/**
 * 扩展模型同步预览中的单条模型摘要。
 */
@Data
public class ExtensionModelSyncPreviewRowDTO {
    private String modelKey;
    private String modelName;
    private String modelType;
    private String provider;
}
