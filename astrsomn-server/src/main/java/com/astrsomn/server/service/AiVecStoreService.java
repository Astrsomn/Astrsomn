package com.astrsomn.server.service;

import com.astrsomn.api.vector.dto.vecstore.*;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiVecStoreService extends IService<AiVecStoreEntity> {

    BaseResponse<String> create(AiVecStoreCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiVecStoreUpdateRequestDTO request);

    PageResponse<AiVecStoreResponseDTO> queryPage(BasePageRequest<AiVecStoreQueryRequestDTO> request);

    BaseResponse<AiVecStoreResponseDTO> detail(Long id);

    BaseResponse<AiVecStoreStatsResponseDTO> stats(Long id);
}
