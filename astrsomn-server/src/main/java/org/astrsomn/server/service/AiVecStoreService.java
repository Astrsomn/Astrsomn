package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.vecstore.AiVecStoreCreateRequestDTO;
import org.astrsomn.core.common.dto.vecstore.AiVecStoreQueryRequestDTO;
import org.astrsomn.core.common.dto.vecstore.AiVecStoreResponseDTO;
import org.astrsomn.core.common.dto.vecstore.AiVecStoreUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiVecStoreEntity;

public interface AiVecStoreService extends IService<AiVecStoreEntity> {

    BaseResponse<String> create(AiVecStoreCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiVecStoreUpdateRequestDTO request);

    PageResponse<AiVecStoreResponseDTO> queryPage(BasePageRequest<AiVecStoreQueryRequestDTO> request);

    BaseResponse<AiVecStoreResponseDTO> detail(Long id);
}
