package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocCreateRequestDTO;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocQueryRequestDTO;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocResponseDTO;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiVecDocEntity;

public interface AiVecDocService extends IService<AiVecDocEntity> {

    BaseResponse<String> create(AiVecDocCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiVecDocUpdateRequestDTO request);

    PageResponse<AiVecDocResponseDTO> queryPage(BasePageRequest<AiVecDocQueryRequestDTO> request);

    BaseResponse<AiVecDocResponseDTO> detail(Long id);
}
