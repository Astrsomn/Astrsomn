package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.env.SystemEnvCreateRequestDTO;
import org.astrsomn.core.common.dto.env.SystemEnvQueryRequestDTO;
import org.astrsomn.core.common.dto.env.SystemEnvUpdateRequestDTO;
import org.astrsomn.core.common.dto.env.SystemEnvResponseDTO;
import org.astrsomn.core.common.entity.SystemEnvEntity;

public interface SystemEnvService extends IService<SystemEnvEntity> {
    BaseResponse<String> create(SystemEnvCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<SystemEnvResponseDTO> detail(Long id);

    BaseResponse<String> update(SystemEnvUpdateRequestDTO request);

    PageResponse<SystemEnvResponseDTO> queryPage(BasePageRequest<SystemEnvQueryRequestDTO> request);
}
