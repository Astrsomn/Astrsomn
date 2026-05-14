package com.astrsomn.api.runtime.common.dto.extension;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class ExtensionModelUnloadPreviewDTO {
    private List<ExtensionModelSyncPreviewRowDTO> toRemove = new ArrayList<>();
    private List<ExtensionModelSyncPreviewRowDTO> keptReferenced = new ArrayList<>();
}