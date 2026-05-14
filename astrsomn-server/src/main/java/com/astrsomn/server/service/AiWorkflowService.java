package com.astrsomn.server.service;

import com.astrsomn.api.workflow.domain.dto.definition.AstFlowDefinitionCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.definition.AstFlowDefinitionQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.definition.AstFlowDefinitionResponseDTO;
import com.astrsomn.api.workflow.domain.dto.definition.AstFlowDefinitionUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowDefinitionEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowService extends IService<AstFlowDefinitionEntity> {

    BaseResponse<String> create(AstFlowDefinitionCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowDefinitionUpdateRequestDTO request);

    BaseResponse<AstFlowDefinitionResponseDTO> detail(Long id);

    PageResponse<AstFlowDefinitionResponseDTO> queryPage(BasePageRequest<AstFlowDefinitionQueryRequestDTO> request);
}
