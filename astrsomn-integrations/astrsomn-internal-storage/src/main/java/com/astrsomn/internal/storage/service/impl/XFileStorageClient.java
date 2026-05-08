package com.astrsomn.internal.storage.service.impl;

import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.internal.storage.config.StorageProperties;
import com.astrsomn.api.storage.exception.AstFileErrorEnum;
import com.astrsomn.internal.storage.service.AstrsomnStorageClient;
import com.astrsomn.internal.storage.service.model.StorageDownloadRequest;
import com.astrsomn.internal.storage.service.model.StorageUploadRequest;
import com.astrsomn.internal.storage.service.model.StorageUploadResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class XFileStorageClient implements AstrsomnStorageClient {

    private static final DateTimeFormatter DATE_PATH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final FileStorageService fileStorageService;
    private final StorageProperties storageProperties;
    private final Environment environment;

    @Override
    public StorageUploadResult upload(StorageUploadRequest request) {
        MultipartFile file = request.getFile();
        if (file == null || file.isEmpty()) {
            throw new BusinessException(AstFileErrorEnum.FILE_UPLOAD_FAILED, "文件不能为空");
        }
        String originalName = file.getOriginalFilename();
        if (StringUtils.isBlank(originalName)) {
            originalName = "unknown";
        }
        String ext = extractExt(originalName);
        String bizType = StringUtils.trimToNull(request.getBizType());
        if (bizType == null) {
            bizType = "common";
        }

        String datePath = buildDatePath();
        String saveFilename = buildSaveFilename(originalName, ext, storageProperties.isRandomFilename());
        String platform = storageProperties.getDefaultPlatform();
        log.info(
                "x-file-storage upload start bizType={} datePath={} filename={} platform={} astrsomn.default-platform={} dromara.default-platform={} activeProfiles={} fileStorageServiceClass={}",
                bizType,
                datePath,
                saveFilename,
                platform,
                environment.getProperty("astrsomn.storage.default-platform"),
                environment.getProperty("dromara.x-file-storage.default-platform"),
                Arrays.toString(environment.getActiveProfiles()),
                fileStorageService.getClass().getName());
        try {
            FileInfo fileInfo = fileStorageService.of(file)
                    .setPlatform(platform)
                    .setPath(bizType + "/" + datePath + "/")
                    .setSaveFilename(saveFilename)
                    .setObjectId(request.getObjectId())
                    .setObjectType(request.getObjectType())
                    .upload();
            if (fileInfo == null) {
                throw new BusinessException(AstFileErrorEnum.FILE_UPLOAD_FAILED, "上传失败，返回为空");
            }
            return StorageUploadResult.builder()
                    .platform(fileInfo.getPlatform())
                    .bucket(null)
                    .objectKey(fileInfo.getBasePath() + fileInfo.getPath() + fileInfo.getFilename())
                    .filename(fileInfo.getFilename())
                    .originalFilename(fileInfo.getOriginalFilename())
                    .ext(fileInfo.getExt())
                    .contentType(fileInfo.getContentType())
                    .size(fileInfo.getSize())
                    .url(fileInfo.getUrl())
                    .etag(null)
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "x-file-storage upload failed bizType={} datePath={} filename={} platform={} astrsomn.default-platform={} dromara.default-platform={} activeProfiles={} fileStorageServiceClass={}",
                    bizType,
                    datePath,
                    saveFilename,
                    platform,
                    environment.getProperty("astrsomn.storage.default-platform"),
                    environment.getProperty("dromara.x-file-storage.default-platform"),
                    Arrays.toString(environment.getActiveProfiles()),
                    fileStorageService.getClass().getName(),
                    e);
            throw new BusinessException(AstFileErrorEnum.FILE_UPLOAD_FAILED, e.getMessage());
        }
    }

    @Override
    public InputStream openInputStream(StorageDownloadRequest request) {
        String platform = StringUtils.trimToNull(request.getPlatform());
        if (platform == null) {
            platform = storageProperties.getDefaultPlatform();
        }
        String objectKey = StringUtils.trimToNull(request.getObjectKey());
        if (objectKey == null) {
            throw new BusinessException(AstFileErrorEnum.FILE_NOT_FOUND, "objectKey 不能为空");
        }
        try {
            return new ByteArrayInputStream(fileStorageService.download(objectKey).bytes());
        } catch (Exception e) {
            log.error("x-file-storage open stream failed key={}", objectKey, e);
            throw new BusinessException(AstFileErrorEnum.FILE_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public void delete(StorageDownloadRequest request) {
        String platform = StringUtils.trimToNull(request.getPlatform());
        if (platform == null) {
            platform = storageProperties.getDefaultPlatform();
        }
        String objectKey = StringUtils.trimToNull(request.getObjectKey());
        if (objectKey == null) {
            return;
        }
        try {
            fileStorageService.delete(objectKey);
        } catch (Exception e) {
            log.warn("x-file-storage delete failed key={} platform={}", objectKey, platform, e);
            throw new BusinessException(AstFileErrorEnum.FILE_DELETE_FAILED, e.getMessage());
        }
    }

    private static String extractExt(String originalName) {
        int idx = originalName.lastIndexOf('.');
        if (idx < 0 || idx == originalName.length() - 1) {
            return "";
        }
        return originalName.substring(idx + 1).toLowerCase();
    }

    private static String sanitize(String originalName) {
        return originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static String buildDatePath() {
        LocalDate now = LocalDate.now();
        return now.format(DATE_PATH_FORMATTER);
    }

    private static String buildSaveFilename(String originalName, String ext, boolean randomFilename) {
        String safeName = sanitize(originalName);
        if (!ext.isEmpty() && !safeName.toLowerCase().endsWith("." + ext)) {
            safeName = safeName + "." + ext;
        }
        if (!randomFilename) {
            return safeName;
        }
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid + "_" + safeName;
    }
}
