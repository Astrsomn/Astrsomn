package com.astrsomn.server.service;

import com.astrsomn.api.runtime.common.dto.sensitiveword.AiSensitiveWordCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.sensitiveword.AiSensitiveWordQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.sensitiveword.AiSensitiveWordResponseDTO;
import com.astrsomn.api.runtime.common.dto.sensitiveword.AiSensitiveWordUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiSensitiveWordEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiSensitiveWordService extends IService<AiSensitiveWordEntity> {
    BaseResponse<String> create(AiSensitiveWordCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiSensitiveWordResponseDTO> detail(Long id);

    BaseResponse<String> update(AiSensitiveWordUpdateRequestDTO request);

    PageResponse<AiSensitiveWordResponseDTO> queryPage(BasePageRequest<AiSensitiveWordQueryRequestDTO> request);
}
