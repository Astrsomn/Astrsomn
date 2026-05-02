package com.astrsomn.internal.storage.service;

import com.astrsomn.internal.storage.service.model.StorageDownloadRequest;
import com.astrsomn.internal.storage.service.model.StorageUploadRequest;
import com.astrsomn.internal.storage.service.model.StorageUploadResult;

import java.io.InputStream;

public interface AstrsomnStorageClient {

    StorageUploadResult upload(StorageUploadRequest request);

    InputStream openInputStream(StorageDownloadRequest request);

    void delete(StorageDownloadRequest request);
}
