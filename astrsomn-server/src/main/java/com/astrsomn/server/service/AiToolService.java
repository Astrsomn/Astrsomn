package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.tool.AiToolCreateRequestDTO;
import com.astrsomn.core.common.dto.tool.AiToolQueryRequestDTO;
import com.astrsomn.core.common.dto.tool.AiToolResponseDTO;
import com.astrsomn.core.common.dto.tool.AiToolUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiToolEntity;

public interface AiToolService extends IService<AiToolEntity> {
    BaseResponse<String> create(AiToolCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiToolResponseDTO> detail(Long id);

    BaseResponse<String> update(AiToolUpdateRequestDTO request);

    PageResponse<AiToolResponseDTO> queryPage(BasePageRequest<AiToolQueryRequestDTO> request);
}
