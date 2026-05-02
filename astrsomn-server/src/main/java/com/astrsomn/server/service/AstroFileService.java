package com.astrsomn.server.service;

import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.api.storage.dto.AstroFileUploadResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AstroFileService {

    BaseResponse<AstroFileUploadResponseDTO> upload(MultipartFile file, String bizType, String bizId);
}
