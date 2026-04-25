package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.mcp.AiMcpCreateRequestDTO;
import com.astrsomn.core.common.dto.mcp.AiMcpQueryRequestDTO;
import com.astrsomn.core.common.dto.mcp.AiMcpResponseDTO;
import com.astrsomn.core.common.dto.mcp.AiMcpUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiMcpEntity;

public interface AiMcpService extends IService<AiMcpEntity> {
    BaseResponse<String> create(AiMcpCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiMcpResponseDTO> detail(Long id);

    BaseResponse<String> update(AiMcpUpdateRequestDTO request);

    PageResponse<AiMcpResponseDTO> queryPage(BasePageRequest<AiMcpQueryRequestDTO> request);
}
