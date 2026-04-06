package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.vecsegment.AiVecSegmentCreateRequestDTO;
import org.astrsomn.core.common.dto.vecsegment.AiVecSegmentQueryRequestDTO;
import org.astrsomn.core.common.dto.vecsegment.AiVecSegmentResponseDTO;
import org.astrsomn.core.common.dto.vecsegment.AiVecSegmentUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiVecSegmentEntity;

public interface AiVecSegmentService extends IService<AiVecSegmentEntity> {

    BaseResponse<String> create(AiVecSegmentCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiVecSegmentUpdateRequestDTO request);

    PageResponse<AiVecSegmentResponseDTO> queryPage(BasePageRequest<AiVecSegmentQueryRequestDTO> request);

    BaseResponse<AiVecSegmentResponseDTO> detail(Long id);
}
