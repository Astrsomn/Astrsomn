package com.astrsomn.internal.storage.service.impl;

import com.astrsomn.api.storage.entity.AstFileRecordEntity;
import com.astrsomn.internal.storage.mapper.AstroFileRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.recorder.FileRecorder;
import org.dromara.x.file.storage.core.upload.FilePartInfo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AstFileRecorder implements FileRecorder {

    private final AstroFileRecordMapper fileRecordMapper;

    @Override
    public boolean save(FileInfo fileInfo) {

        return true;
    }

    @Override
    public void update(FileInfo fileInfo) {

    }

    @Override
    public FileInfo getByUrl(String url) {
        AstFileRecordEntity record = fileRecordMapper.selectOne(
                new LambdaQueryWrapper<AstFileRecordEntity>()
                        .eq(AstFileRecordEntity::getFileUrl, url)
                        .last("LIMIT 1")
        );
        if (record == null) {
            return null;
        }
        FileInfo fileInfo = new FileInfo();
        fileInfo.setPlatform(record.getPlatform());
        fileInfo.setUrl(record.getFileUrl());
        String objectKey = record.getObjectKey();
        if (objectKey != null) {
            int lastSlash = objectKey.lastIndexOf('/');
            if (lastSlash >= 0) {
                fileInfo.setPath(objectKey.substring(0, lastSlash + 1));
                fileInfo.setFilename(objectKey.substring(lastSlash + 1));
            } else {
                fileInfo.setPath("");
                fileInfo.setFilename(objectKey);
            }
        }
        fileInfo.setOriginalFilename(record.getOriginName());
        fileInfo.setContentType(record.getMimeType());
        fileInfo.setSize(record.getFileSize());
        return fileInfo;
    }

    @Override
    public boolean delete(String url) {
        return true;
    }

    @Override
    public void saveFilePart(FilePartInfo filePartInfo) {

    }

    @Override
    public void deleteFilePartByUploadId(String uploadId) {

    }
}
