package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.instance.AiInstanceCreateRequestDTO;
import com.astrsomn.core.common.dto.instance.AiInstanceQueryRequestDTO;
import com.astrsomn.core.common.dto.instance.AiInstanceResponseDTO;
import com.astrsomn.core.common.dto.instance.AiInstanceUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiInstanceEntity;

public interface AiInstanceService extends IService<AiInstanceEntity> {

    BaseResponse<String> create(AiInstanceCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiInstanceResponseDTO> detail(Long id);

    BaseResponse<String> update(AiInstanceUpdateRequestDTO request);

    PageResponse<AiInstanceResponseDTO> queryPage(BasePageRequest<AiInstanceQueryRequestDTO> request);
}
