package com.astrsomn.server.service.ai;

import com.astrsomn.api.runtime.common.dto.mcp.AiMcpCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.mcp.AiMcpQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.mcp.AiMcpResponseDTO;
import com.astrsomn.api.runtime.common.dto.mcp.AiMcpUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiMcpEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiMcpService extends IService<AiMcpEntity> {
    BaseResponse<String> create(AiMcpCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiMcpResponseDTO> detail(Long id);

    BaseResponse<String> update(AiMcpUpdateRequestDTO request);

    PageResponse<AiMcpResponseDTO> queryPage(BasePageRequest<AiMcpQueryRequestDTO> request);
}
