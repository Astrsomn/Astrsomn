package com.astrsomn.server.service;

import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.api.storage.dto.AstFileUploadResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AstroFileService {

    BaseResponse<AstFileUploadResponseDTO> upload(MultipartFile file, String bizType, String bizId);
}
