package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.model.AiModelCreateRequestDTO;
import org.astrsomn.core.common.dto.model.AiModelQueryRequestDTO;
import org.astrsomn.core.common.dto.model.AiModelResponseDTO;
import org.astrsomn.core.common.dto.model.AiModelUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiModelEntity;

public interface AiModelService extends IService<AiModelEntity> {
    BaseResponse<String> delete(long[] longIds);

    PageResponse<AiModelResponseDTO> queryPage(BasePageRequest<AiModelQueryRequestDTO> request);

    BaseResponse<AiModelResponseDTO> detail(Long longId);

    BaseResponse<String> updateModel(AiModelUpdateRequestDTO request);

    BaseResponse<String> create(AiModelCreateRequestDTO request);
}
