package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.dto.systemmessage.SystemMessageCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.systemmessage.SystemMessageQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.systemmessage.SystemMessageResponseDTO;
import com.astrsomn.api.runtime.common.dto.systemmessage.SystemMessageUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.SystemMessageEntity;

public interface SystemMessageService extends IService<SystemMessageEntity> {

    BaseResponse<String> create(SystemMessageCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(SystemMessageUpdateRequestDTO request);

    PageResponse<SystemMessageResponseDTO> queryPage(BasePageRequest<SystemMessageQueryRequestDTO> request);

    BaseResponse<SystemMessageResponseDTO> detail(Long id);
}
