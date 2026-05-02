package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.dto.env.SystemEnvCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.env.SystemEnvQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.env.SystemEnvResponseDTO;
import com.astrsomn.api.runtime.common.dto.env.SystemEnvUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.SystemEnvEntity;

public interface SystemEnvService extends IService<SystemEnvEntity> {
    BaseResponse<String> create(SystemEnvCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<SystemEnvResponseDTO> detail(Long id);

    BaseResponse<String> update(SystemEnvUpdateRequestDTO request);

    PageResponse<SystemEnvResponseDTO> queryPage(BasePageRequest<SystemEnvQueryRequestDTO> request);
}
