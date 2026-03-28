package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.config.SystemConfigCreateRequestDTO;
import org.astrsomn.core.common.dto.config.SystemConfigQueryRequestDTO;
import org.astrsomn.core.common.dto.config.SystemConfigResponseDTO;
import org.astrsomn.core.common.dto.config.SystemConfigUpdateRequestDTO;
import org.astrsomn.core.common.entity.SystemConfigEntity;

public interface SystemConfigService extends IService<SystemConfigEntity> {
    BaseResponse<String> create(SystemConfigCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<SystemConfigResponseDTO> detail(Long id);

    BaseResponse<String> update(SystemConfigUpdateRequestDTO request);

    PageResponse<SystemConfigResponseDTO> queryPage(BasePageRequest<SystemConfigQueryRequestDTO> request);
}
