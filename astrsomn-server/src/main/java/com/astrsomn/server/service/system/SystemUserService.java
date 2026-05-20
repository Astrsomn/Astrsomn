package com.astrsomn.server.service.system;

import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.system.dto.user.SystemUserCreateRequestDTO;
import com.astrsomn.system.dto.user.SystemUserQueryRequestDTO;
import com.astrsomn.system.dto.user.SystemUserResponseDTO;
import com.astrsomn.system.dto.user.SystemUserUpdateRequestDTO;
import com.astrsomn.system.entity.SystemUserEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SystemUserService extends IService<SystemUserEntity> {
    BaseResponse<String> create(SystemUserCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<SystemUserResponseDTO> detail(Long id);

    BaseResponse<String> update(SystemUserUpdateRequestDTO request);

    PageResponse<SystemUserResponseDTO> queryPage(BasePageRequest<SystemUserQueryRequestDTO> request);
}
