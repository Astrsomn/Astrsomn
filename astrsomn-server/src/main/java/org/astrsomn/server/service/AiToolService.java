package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.tool.AiToolCreateRequestDTO;
import org.astrsomn.core.common.dto.tool.AiToolQueryRequestDTO;
import org.astrsomn.core.common.dto.tool.AiToolResponseDTO;
import org.astrsomn.core.common.dto.tool.AiToolUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiToolEntity;

public interface AiToolService extends IService<AiToolEntity> {
    BaseResponse<String> create(AiToolCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiToolResponseDTO> detail(Long id);

    BaseResponse<String> update(AiToolUpdateRequestDTO request);

    PageResponse<AiToolResponseDTO> queryPage(BasePageRequest<AiToolQueryRequestDTO> request);
}
