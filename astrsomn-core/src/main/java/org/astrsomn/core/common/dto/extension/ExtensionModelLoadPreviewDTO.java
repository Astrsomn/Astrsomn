package org.astrsomn.core.common.dto.extension;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 「加载模型」预览：将新增入库 vs 已存在跳过；无效条目仅计数（如无 modelKey）。
 */
@Data
public class ExtensionModelLoadPreviewDTO {
    private List<ExtensionModelSyncPreviewRowDTO> toCreate = new ArrayList<>();
    private List<ExtensionModelSyncPreviewRowDTO> skippedExisting = new ArrayList<>();
    private int skippedInvalidCount;
}
