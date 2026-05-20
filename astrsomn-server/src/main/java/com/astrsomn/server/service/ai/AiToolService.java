package com.astrsomn.server.service.ai;

import com.astrsomn.api.runtime.common.dto.tool.AiToolCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.tool.AiToolQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.tool.AiToolResponseDTO;
import com.astrsomn.api.runtime.common.dto.tool.AiToolUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiToolEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiToolService extends IService<AiToolEntity> {
    BaseResponse<String> create(AiToolCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiToolResponseDTO> detail(Long id);

    BaseResponse<String> update(AiToolUpdateRequestDTO request);

    PageResponse<AiToolResponseDTO> queryPage(BasePageRequest<AiToolQueryRequestDTO> request);
}
