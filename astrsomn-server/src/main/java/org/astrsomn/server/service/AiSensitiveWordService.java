package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordCreateRequestDTO;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordQueryRequestDTO;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordResponseDTO;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiSensitiveWordEntity;

public interface AiSensitiveWordService extends IService<AiSensitiveWordEntity> {
    BaseResponse<String> create(AiSensitiveWordCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiSensitiveWordResponseDTO> detail(Long id);

    BaseResponse<String> update(AiSensitiveWordUpdateRequestDTO request);

    PageResponse<AiSensitiveWordResponseDTO> queryPage(BasePageRequest<AiSensitiveWordQueryRequestDTO> request);
}
