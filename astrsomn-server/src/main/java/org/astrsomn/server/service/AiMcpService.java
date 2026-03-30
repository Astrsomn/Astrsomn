package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.mcp.AiMcpCreateRequestDTO;
import org.astrsomn.core.common.dto.mcp.AiMcpQueryRequestDTO;
import org.astrsomn.core.common.dto.mcp.AiMcpUpdateRequestDTO;
import org.astrsomn.core.common.dto.mcp.AiMcpResponseDTO;
import org.astrsomn.core.common.entity.AiMcpEntity;

public interface AiMcpService extends IService<AiMcpEntity> {
    BaseResponse<String> create(AiMcpCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiMcpResponseDTO> detail(Long id);

    BaseResponse<String> update(AiMcpUpdateRequestDTO request);

    PageResponse<AiMcpResponseDTO> queryPage(BasePageRequest<AiMcpQueryRequestDTO> request);
}
