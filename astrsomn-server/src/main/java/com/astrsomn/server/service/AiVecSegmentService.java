package com.astrsomn.server.service;

import com.astrsomn.api.vector.dto.vecsegment.AiVecSegmentCreateRequestDTO;
import com.astrsomn.api.vector.dto.vecsegment.AiVecSegmentQueryRequestDTO;
import com.astrsomn.api.vector.dto.vecsegment.AiVecSegmentResponseDTO;
import com.astrsomn.api.vector.dto.vecsegment.AiVecSegmentUpdateRequestDTO;
import com.astrsomn.api.vector.entity.AiVecSegmentEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiVecSegmentService extends IService<AiVecSegmentEntity> {

    BaseResponse<String> create(AiVecSegmentCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiVecSegmentUpdateRequestDTO request);

    PageResponse<AiVecSegmentResponseDTO> queryPage(BasePageRequest<AiVecSegmentQueryRequestDTO> request);

    BaseResponse<AiVecSegmentResponseDTO> detail(Long id);
}
