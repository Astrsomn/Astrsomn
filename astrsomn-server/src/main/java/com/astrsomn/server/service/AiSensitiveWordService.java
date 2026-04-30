package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordCreateRequestDTO;
import com.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordQueryRequestDTO;
import com.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordResponseDTO;
import com.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiSensitiveWordEntity;

public interface AiSensitiveWordService extends IService<AiSensitiveWordEntity> {
    BaseResponse<String> create(AiSensitiveWordCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiSensitiveWordResponseDTO> detail(Long id);

    BaseResponse<String> update(AiSensitiveWordUpdateRequestDTO request);

    PageResponse<AiSensitiveWordResponseDTO> queryPage(BasePageRequest<AiSensitiveWordQueryRequestDTO> request);
}
