package com.astrsomn.api.runtime.common.dto.extension;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 「卸载模型」预览：将删除 vs 因实例引用保留。
 */
@Data
public class ExtensionModelUnloadPreviewDTO {
    private List<ExtensionModelSyncPreviewRowDTO> toRemove = new ArrayList<>();
    private List<ExtensionModelSyncPreviewRowDTO> keptReferenced = new ArrayList<>();
}
