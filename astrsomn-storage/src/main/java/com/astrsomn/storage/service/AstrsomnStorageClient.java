package com.astrsomn.storage.service;

import com.astrsomn.storage.service.model.StorageDownloadRequest;
import com.astrsomn.storage.service.model.StorageUploadRequest;
import com.astrsomn.storage.service.model.StorageUploadResult;

import java.io.InputStream;

public interface AstrsomnStorageClient {

    StorageUploadResult upload(StorageUploadRequest request);

    InputStream openInputStream(StorageDownloadRequest request);

    void delete(StorageDownloadRequest request);
}
