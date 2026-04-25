package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.config.SystemConfigCreateRequestDTO;
import com.astrsomn.core.common.dto.config.SystemConfigQueryRequestDTO;
import com.astrsomn.core.common.dto.config.SystemConfigResponseDTO;
import com.astrsomn.core.common.dto.config.SystemConfigUpdateRequestDTO;
import com.astrsomn.core.common.entity.SystemConfigEntity;

public interface SystemConfigService extends IService<SystemConfigEntity> {
    BaseResponse<String> create(SystemConfigCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<SystemConfigResponseDTO> detail(Long id);

    BaseResponse<String> update(SystemConfigUpdateRequestDTO request);

    PageResponse<SystemConfigResponseDTO> queryPage(BasePageRequest<SystemConfigQueryRequestDTO> request);
}
