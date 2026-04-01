package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.extension.SystemExtensionCreateRequestDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionQueryRequestDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionResponseDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionUpdateRequestDTO;
import org.astrsomn.core.common.entity.SystemExtensionEntity;

public interface SystemExtensionService extends IService<SystemExtensionEntity> {

    BaseResponse<String> create(SystemExtensionCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<SystemExtensionResponseDTO> detail(Long id);

    BaseResponse<String> update(SystemExtensionUpdateRequestDTO request);

    PageResponse<SystemExtensionResponseDTO> queryPage(BasePageRequest<SystemExtensionQueryRequestDTO> request);

    BaseResponse<String> apply(Long id);

    BaseResponse<String> uninstall(Long id);
}
