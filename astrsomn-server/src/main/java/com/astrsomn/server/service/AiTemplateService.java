package com.astrsomn.server.service;

import com.astrsomn.api.runtime.common.dto.template.AiTemplateCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.template.AiTemplateQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.template.AiTemplateResponseDTO;
import com.astrsomn.api.runtime.common.dto.template.AiTemplateUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiTemplateEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiTemplateService extends IService<AiTemplateEntity> {
    BaseResponse<String> create(AiTemplateCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiTemplateResponseDTO> detail(Long id);

    BaseResponse<String> update(AiTemplateUpdateRequestDTO request);

    PageResponse<AiTemplateResponseDTO> queryPage(BasePageRequest<AiTemplateQueryRequestDTO> request);
}
