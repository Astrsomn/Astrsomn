package com.astrsomn.server.service.extension.base;

import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.system.dto.extension.SystemExtensionCreateRequestDTO;
import com.astrsomn.system.dto.extension.SystemExtensionQueryRequestDTO;
import com.astrsomn.system.dto.extension.SystemExtensionResponseDTO;
import com.astrsomn.system.dto.extension.SystemExtensionUpdateRequestDTO;
import com.astrsomn.system.entity.SystemExtensionEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

public interface SystemExtensionService extends IService<SystemExtensionEntity> {

    BaseResponse<String> create(SystemExtensionCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<SystemExtensionResponseDTO> detail(Long id);

    BaseResponse<String> update(SystemExtensionUpdateRequestDTO request);

    PageResponse<SystemExtensionResponseDTO> queryPage(BasePageRequest<SystemExtensionQueryRequestDTO> request);

    BaseResponse<String> apply(Long id);

    
    BaseResponse<String> revokeApply(Long id);

    
    BaseResponse<String> uninstall(Long id);

    
    BaseResponse<String> uploadJar(MultipartFile file);
}
