package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.core.common.base.BasePageRequest;
import com.astrsomn.core.common.base.BaseResponse;
import com.astrsomn.core.common.base.PageResponse;
import com.astrsomn.core.common.dto.model.AiModelCreateRequestDTO;
import com.astrsomn.core.common.dto.model.AiModelQueryRequestDTO;
import com.astrsomn.core.common.dto.model.AiModelResponseDTO;
import com.astrsomn.core.common.dto.model.AiModelUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiModelEntity;

public interface AiModelService extends IService<AiModelEntity> {
    BaseResponse<String> delete(long[] longIds);

    PageResponse<AiModelResponseDTO> queryPage(BasePageRequest<AiModelQueryRequestDTO> request);

    BaseResponse<AiModelResponseDTO> detail(Long longId);

    BaseResponse<String> updateModel(AiModelUpdateRequestDTO request);

    BaseResponse<String> create(AiModelCreateRequestDTO request);
}
