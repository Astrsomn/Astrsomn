package com.astrsomn.server.service.impl;

import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.server.service.AstroFileRecordService;
import com.astrsomn.server.service.AstroFileService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.storage.core.dto.AstroFileUploadResponseDTO;
import com.astrsomn.storage.core.entity.AstroFileRecordEntity;
import com.astrsomn.storage.core.exception.AstroFileErrorEnum;
import com.astrsomn.storage.service.AstrsomnStorageClient;
import com.astrsomn.storage.service.model.StorageUploadRequest;
import com.astrsomn.storage.service.model.StorageUploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AstroFileServiceImpl implements AstroFileService {

    private final AstrsomnStorageClient astrsomnStorageClient;
    private final AstroFileRecordService astroFileRecordService;
    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<AstroFileUploadResponseDTO> upload(MultipartFile file, String bizType, String bizId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(AstroFileErrorEnum.FILE_PARAM_ERROR, "文件不能为空");
        }
        String normalizedBizType = StringUtils.trimToNull(bizType);
        if (normalizedBizType == null) {
            normalizedBizType = "common";
        }
        StorageUploadResult uploadResult = astrsomnStorageClient.upload(StorageUploadRequest.builder()
                .file(file)
                .bizType(normalizedBizType)
                .objectId(StringUtils.trimToNull(bizId))
                .objectType(normalizedBizType)
                .build());
        AstroFileRecordEntity row = new AstroFileRecordEntity();
        row.setBizType(normalizedBizType);
        row.setBizId(StringUtils.trimToNull(bizId));
        row.setPlatform(uploadResult.getPlatform());
        row.setBucket(uploadResult.getBucket());
        row.setObjectKey(uploadResult.getObjectKey());
        row.setOriginName(uploadResult.getOriginalFilename());
        row.setMimeType(uploadResult.getContentType());
        row.setFileSize(uploadResult.getSize());
        row.setEtag(uploadResult.getEtag());
        row.setFileUrl(uploadResult.getUrl());
        row.setStatus("ACTIVE");
        queryEnvParamHelper.stampEffectiveEnv(row);
        boolean saved = astroFileRecordService.save(row);
        if (!saved) {
            throw new BusinessException(AstroFileErrorEnum.FILE_RECORD_CREATE_FAILED);
        }
        AstroFileUploadResponseDTO dto = new AstroFileUploadResponseDTO();
        BeanUtils.copyProperties(row, dto);
        return BaseResponse.success(dto);
    }
}
