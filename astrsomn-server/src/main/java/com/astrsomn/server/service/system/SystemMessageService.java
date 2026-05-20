package com.astrsomn.server.service.system;

import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.system.dto.systemmessage.SystemMessageCreateRequestDTO;
import com.astrsomn.system.dto.systemmessage.SystemMessageQueryRequestDTO;
import com.astrsomn.system.dto.systemmessage.SystemMessageResponseDTO;
import com.astrsomn.system.dto.systemmessage.SystemMessageUpdateRequestDTO;
import com.astrsomn.system.entity.SystemMessageEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SystemMessageService extends IService<SystemMessageEntity> {

    BaseResponse<String> create(SystemMessageCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(SystemMessageUpdateRequestDTO request);

    PageResponse<SystemMessageResponseDTO> queryPage(BasePageRequest<SystemMessageQueryRequestDTO> request);

    BaseResponse<SystemMessageResponseDTO> detail(Long id);
}
