package com.astrsomn.internal.storage.service.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class StorageUploadRequest {

    private MultipartFile file;
    private String bizType;
    private String objectId;
    private String objectType;
}
