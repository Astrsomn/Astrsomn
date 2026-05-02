package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.dto.config.SystemConfigCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.config.SystemConfigQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.config.SystemConfigResponseDTO;
import com.astrsomn.api.runtime.common.dto.config.SystemConfigUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.SystemConfigEntity;

public interface SystemConfigService extends IService<SystemConfigEntity> {
    BaseResponse<String> create(SystemConfigCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<SystemConfigResponseDTO> detail(Long id);

    BaseResponse<String> update(SystemConfigUpdateRequestDTO request);

    PageResponse<SystemConfigResponseDTO> queryPage(BasePageRequest<SystemConfigQueryRequestDTO> request);
}
