package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.model.AiModelCreateRequestDTO;
import com.astrsomn.core.common.dto.model.AiModelQueryRequestDTO;
import com.astrsomn.core.common.dto.model.AiModelResponseDTO;
import com.astrsomn.core.common.dto.model.AiModelUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiModelEntity;

public interface AiModelService extends IService<AiModelEntity> {
    BaseResponse<String> delete(long[] longIds);

    BaseResponse<String> generateInstances(long[] modelIds);

    PageResponse<AiModelResponseDTO> queryPage(BasePageRequest<AiModelQueryRequestDTO> request);

    BaseResponse<AiModelResponseDTO> detail(Long longId);

    BaseResponse<String> updateModel(AiModelUpdateRequestDTO request);

    BaseResponse<String> create(AiModelCreateRequestDTO request);
}
