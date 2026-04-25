package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.vecstore.AiVecStoreCreateRequestDTO;
import com.astrsomn.core.common.dto.vecstore.AiVecStoreQueryRequestDTO;
import com.astrsomn.core.common.dto.vecstore.AiVecStoreResponseDTO;
import com.astrsomn.core.common.dto.vecstore.AiVecStoreUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiVecStoreEntity;

public interface AiVecStoreService extends IService<AiVecStoreEntity> {

    BaseResponse<String> create(AiVecStoreCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiVecStoreUpdateRequestDTO request);

    PageResponse<AiVecStoreResponseDTO> queryPage(BasePageRequest<AiVecStoreQueryRequestDTO> request);

    BaseResponse<AiVecStoreResponseDTO> detail(Long id);
}
