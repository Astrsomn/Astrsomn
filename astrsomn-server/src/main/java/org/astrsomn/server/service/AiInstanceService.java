package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.instance.AiInstanceCreateRequestDTO;
import org.astrsomn.core.common.dto.instance.AiInstanceQueryRequestDTO;
import org.astrsomn.core.common.dto.instance.AiInstanceResponseDTO;
import org.astrsomn.core.common.dto.instance.AiInstanceUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiInstanceEntity;

public interface AiInstanceService extends IService<AiInstanceEntity> {

    BaseResponse<String> create(AiInstanceCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiInstanceResponseDTO> detail(Long id);

    BaseResponse<String> update(AiInstanceUpdateRequestDTO request);

    PageResponse<AiInstanceResponseDTO> queryPage(BasePageRequest<AiInstanceQueryRequestDTO> request);
}
