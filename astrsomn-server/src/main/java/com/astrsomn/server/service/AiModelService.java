package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.dto.model.AiModelCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.model.AiModelQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.model.AiModelResponseDTO;
import com.astrsomn.api.runtime.common.dto.model.AiModelUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;

public interface AiModelService extends IService<AiModelEntity> {
    BaseResponse<String> delete(long[] longIds);

    BaseResponse<String> generateInstances(long[] modelIds);

    PageResponse<AiModelResponseDTO> queryPage(BasePageRequest<AiModelQueryRequestDTO> request);

    BaseResponse<AiModelResponseDTO> detail(Long longId);

    BaseResponse<String> updateModel(AiModelUpdateRequestDTO request);

    BaseResponse<String> create(AiModelCreateRequestDTO request);
}
