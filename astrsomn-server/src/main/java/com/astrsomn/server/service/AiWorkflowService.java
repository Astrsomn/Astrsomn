package com.astrsomn.server.service;

import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.workflow.core.domain.dto.definition.AstFlowDefinitionCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.definition.AstFlowDefinitionQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.definition.AstFlowDefinitionResponseDTO;
import com.astrsomn.workflow.core.domain.dto.definition.AstFlowDefinitionUpdateRequestDTO;
import com.astrsomn.workflow.core.domain.entity.AstFlowDefinitionEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowService extends IService<AstFlowDefinitionEntity> {

    BaseResponse<String> create(AstFlowDefinitionCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowDefinitionUpdateRequestDTO request);

    BaseResponse<AstFlowDefinitionResponseDTO> detail(Long id);

    PageResponse<AstFlowDefinitionResponseDTO> queryPage(BasePageRequest<AstFlowDefinitionQueryRequestDTO> request);
}
