package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.vecdriver.AiVecDriverCreateRequestDTO;
import com.astrsomn.core.common.dto.vecdriver.AiVecDriverQueryRequestDTO;
import com.astrsomn.core.common.dto.vecdriver.AiVecDriverResponseDTO;
import com.astrsomn.core.common.dto.vecdriver.AiVecDriverUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiVecDriverEntity;

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
