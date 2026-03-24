package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.template.AiTemplateCreateRequestDTO;
import org.astrsomn.core.common.dto.template.AiTemplateQueryRequestDTO;
import org.astrsomn.core.common.dto.template.AiTemplateUpdateRequestDTO;
import org.astrsomn.core.common.dto.template.AiTemplateResponseDTO;
import org.astrsomn.core.common.entity.AiTemplateEntity;

public interface AiTemplateService extends IService<AiTemplateEntity> {
    BaseResponse<String> create(AiTemplateCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiTemplateResponseDTO> detail(Long id);

    BaseResponse<String> update(AiTemplateUpdateRequestDTO request);

    PageResponse<AiTemplateResponseDTO> queryPage(BasePageRequest<AiTemplateQueryRequestDTO> request);
}
