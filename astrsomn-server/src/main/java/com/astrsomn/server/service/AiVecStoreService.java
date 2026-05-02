package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.dto.vecstore.AiVecStoreCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.vecstore.AiVecStoreQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.vecstore.AiVecStoreResponseDTO;
import com.astrsomn.api.runtime.common.dto.vecstore.AiVecStoreStatsResponseDTO;
import com.astrsomn.api.runtime.common.dto.vecstore.AiVecStoreUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiVecStoreEntity;

public interface AiVecStoreService extends IService<AiVecStoreEntity> {

    BaseResponse<String> create(AiVecStoreCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiVecStoreUpdateRequestDTO request);

    PageResponse<AiVecStoreResponseDTO> queryPage(BasePageRequest<AiVecStoreQueryRequestDTO> request);

    BaseResponse<AiVecStoreResponseDTO> detail(Long id);

    BaseResponse<AiVecStoreStatsResponseDTO> stats(Long id);
}
