package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.template.AiTemplateCreateRequestDTO;
import com.astrsomn.core.common.dto.template.AiTemplateQueryRequestDTO;
import com.astrsomn.core.common.dto.template.AiTemplateResponseDTO;
import com.astrsomn.core.common.dto.template.AiTemplateUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiTemplateEntity;

public interface AiTemplateService extends IService<AiTemplateEntity> {
    BaseResponse<String> create(AiTemplateCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiTemplateResponseDTO> detail(Long id);

    BaseResponse<String> update(AiTemplateUpdateRequestDTO request);

    PageResponse<AiTemplateResponseDTO> queryPage(BasePageRequest<AiTemplateQueryRequestDTO> request);
}
