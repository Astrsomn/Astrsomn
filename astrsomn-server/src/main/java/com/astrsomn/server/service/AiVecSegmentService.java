package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.dto.vecsegment.AiVecSegmentCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.vecsegment.AiVecSegmentQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.vecsegment.AiVecSegmentResponseDTO;
import com.astrsomn.api.runtime.common.dto.vecsegment.AiVecSegmentUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiVecSegmentEntity;

public interface AiVecSegmentService extends IService<AiVecSegmentEntity> {

    BaseResponse<String> create(AiVecSegmentCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiVecSegmentUpdateRequestDTO request);

    PageResponse<AiVecSegmentResponseDTO> queryPage(BasePageRequest<AiVecSegmentQueryRequestDTO> request);

    BaseResponse<AiVecSegmentResponseDTO> detail(Long id);
}
