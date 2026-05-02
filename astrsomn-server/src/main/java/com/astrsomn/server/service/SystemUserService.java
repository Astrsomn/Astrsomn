package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.dto.user.SystemUserCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.user.SystemUserQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.user.SystemUserResponseDTO;
import com.astrsomn.api.runtime.common.dto.user.SystemUserUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.SystemUserEntity;

public interface SystemUserService extends IService<SystemUserEntity> {
    BaseResponse<String> create(SystemUserCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<SystemUserResponseDTO> detail(Long id);

    BaseResponse<String> update(SystemUserUpdateRequestDTO request);

    PageResponse<SystemUserResponseDTO> queryPage(BasePageRequest<SystemUserQueryRequestDTO> request);
}
