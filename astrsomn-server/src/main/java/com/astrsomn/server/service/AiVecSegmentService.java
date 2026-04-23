package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.core.common.base.BasePageRequest;
import com.astrsomn.core.common.base.BaseResponse;
import com.astrsomn.core.common.base.PageResponse;
import com.astrsomn.core.common.dto.vecsegment.AiVecSegmentCreateRequestDTO;
import com.astrsomn.core.common.dto.vecsegment.AiVecSegmentQueryRequestDTO;
import com.astrsomn.core.common.dto.vecsegment.AiVecSegmentResponseDTO;
import com.astrsomn.core.common.dto.vecsegment.AiVecSegmentUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiVecSegmentEntity;

public interface AiVecSegmentService extends IService<AiVecSegmentEntity> {

    BaseResponse<String> create(AiVecSegmentCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiVecSegmentUpdateRequestDTO request);

    PageResponse<AiVecSegmentResponseDTO> queryPage(BasePageRequest<AiVecSegmentQueryRequestDTO> request);

    BaseResponse<AiVecSegmentResponseDTO> detail(Long id);
}
