package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.core.common.base.BasePageRequest;
import com.astrsomn.core.common.base.BaseResponse;
import com.astrsomn.core.common.base.PageResponse;
import com.astrsomn.core.common.dto.env.SystemEnvCreateRequestDTO;
import com.astrsomn.core.common.dto.env.SystemEnvQueryRequestDTO;
import com.astrsomn.core.common.dto.env.SystemEnvResponseDTO;
import com.astrsomn.core.common.dto.env.SystemEnvUpdateRequestDTO;
import com.astrsomn.core.common.entity.SystemEnvEntity;

public interface SystemEnvService extends IService<SystemEnvEntity> {
    BaseResponse<String> create(SystemEnvCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<SystemEnvResponseDTO> detail(Long id);

    BaseResponse<String> update(SystemEnvUpdateRequestDTO request);

    PageResponse<SystemEnvResponseDTO> queryPage(BasePageRequest<SystemEnvQueryRequestDTO> request);
}
