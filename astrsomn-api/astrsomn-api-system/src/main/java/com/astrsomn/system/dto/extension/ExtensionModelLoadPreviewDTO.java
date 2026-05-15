package com.astrsomn.system.dto.extension;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class ExtensionModelLoadPreviewDTO {
    private List<ExtensionModelSyncPreviewRowDTO> toCreate = new ArrayList<>();
    private List<ExtensionModelSyncPreviewRowDTO> skippedExisting = new ArrayList<>();
    private int skippedInvalidCount;
}