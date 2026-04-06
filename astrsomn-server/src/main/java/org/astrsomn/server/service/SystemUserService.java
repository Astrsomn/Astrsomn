package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.user.SystemUserCreateRequestDTO;
import org.astrsomn.core.common.dto.user.SystemUserQueryRequestDTO;
import org.astrsomn.core.common.dto.user.SystemUserResponseDTO;
import org.astrsomn.core.common.dto.user.SystemUserUpdateRequestDTO;
import org.astrsomn.core.common.entity.SystemUserEntity;

public interface SystemUserService extends IService<SystemUserEntity> {
    BaseResponse<String> create(SystemUserCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<SystemUserResponseDTO> detail(Long id);

    BaseResponse<String> update(SystemUserUpdateRequestDTO request);

    PageResponse<SystemUserResponseDTO> queryPage(BasePageRequest<SystemUserQueryRequestDTO> request);
}
