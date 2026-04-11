package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.vecdriver.AiVecDriverCreateRequestDTO;
import org.astrsomn.core.common.dto.vecdriver.AiVecDriverQueryRequestDTO;
import org.astrsomn.core.common.dto.vecdriver.AiVecDriverResponseDTO;
import org.astrsomn.core.common.dto.vecdriver.AiVecDriverUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiVecDriverEntity;

public interface AiVecDriverService extends IService<AiVecDriverEntity> {

    BaseResponse<String> create(AiVecDriverCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiVecDriverUpdateRequestDTO request);

    PageResponse<AiVecDriverResponseDTO> queryPage(BasePageRequest<AiVecDriverQueryRequestDTO> request);

    BaseResponse<AiVecDriverResponseDTO> detail(Long id);
}
