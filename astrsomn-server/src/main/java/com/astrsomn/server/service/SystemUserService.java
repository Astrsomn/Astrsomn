package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.user.SystemUserCreateRequestDTO;
import com.astrsomn.core.common.dto.user.SystemUserQueryRequestDTO;
import com.astrsomn.core.common.dto.user.SystemUserResponseDTO;
import com.astrsomn.core.common.dto.user.SystemUserUpdateRequestDTO;
import com.astrsomn.core.common.entity.SystemUserEntity;

public interface SystemUserService extends IService<SystemUserEntity> {
    BaseResponse<String> create(SystemUserCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<SystemUserResponseDTO> detail(Long id);

    BaseResponse<String> update(SystemUserUpdateRequestDTO request);

    PageResponse<SystemUserResponseDTO> queryPage(BasePageRequest<SystemUserQueryRequestDTO> request);
}
