package com.astrsomn.server.service;

import com.astrsomn.api.runtime.common.dto.instance.AiInstanceCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceResponseDTO;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AiInstanceService extends IService<AiInstanceEntity> {

    BaseResponse<String> create(AiInstanceCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiInstanceResponseDTO> detail(Long id);

    BaseResponse<String> update(AiInstanceUpdateRequestDTO request);

    PageResponse<AiInstanceResponseDTO> queryPage(BasePageRequest<AiInstanceQueryRequestDTO> request);

    List<AiInstanceResponseDTO> queryByBizKey(String bizKey);
}
