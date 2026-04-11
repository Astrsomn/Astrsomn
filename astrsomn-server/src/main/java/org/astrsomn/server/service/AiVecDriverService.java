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

import java.util.List;

public interface AiVecDriverService extends IService<AiVecDriverEntity> {

    BaseResponse<String> create(AiVecDriverCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiVecDriverUpdateRequestDTO request);

    PageResponse<AiVecDriverResponseDTO> queryPage(BasePageRequest<AiVecDriverQueryRequestDTO> request);

    BaseResponse<AiVecDriverResponseDTO> detail(Long id);

    /**
     * 下拉框等场景：全量驱动列表（无分页），按名称排序。
     */
    BaseResponse<List<AiVecDriverResponseDTO>> listForSelect(String status);
}
