package com.astrsomn.storage.service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StorageUploadResult {
    private String platform;
    private String bucket;
    private String objectKey;
    private String filename;
    private String originalFilename;
    private String ext;
    private String contentType;
    private Long size;
    private String url;
    private String etag;
}
