package com.astrsomn.storage.service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StorageDownloadRequest {
    private String platform;
    private String objectKey;
}
