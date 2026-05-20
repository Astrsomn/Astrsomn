package com.astrsomn.server.service.system;

import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.system.dto.config.SystemConfigCreateRequestDTO;
import com.astrsomn.system.dto.config.SystemConfigQueryRequestDTO;
import com.astrsomn.system.dto.config.SystemConfigResponseDTO;
import com.astrsomn.system.dto.config.SystemConfigUpdateRequestDTO;
import com.astrsomn.system.entity.SystemConfigEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SystemConfigService extends IService<SystemConfigEntity> {
    BaseResponse<String> create(SystemConfigCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<SystemConfigResponseDTO> detail(Long id);

    BaseResponse<String> update(SystemConfigUpdateRequestDTO request);

    PageResponse<SystemConfigResponseDTO> queryPage(BasePageRequest<SystemConfigQueryRequestDTO> request);
}
