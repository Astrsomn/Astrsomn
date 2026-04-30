package com.astrsomn.server.service;

import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.workflow.core.domain.dto.deployment.AstFlowDeploymentCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.deployment.AstFlowDeploymentQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.deployment.AstFlowDeploymentResponseDTO;
import com.astrsomn.workflow.core.domain.dto.deployment.AstFlowDeploymentUpdateRequestDTO;
import com.astrsomn.workflow.core.domain.entity.AstFlowDeploymentEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowDeploymentService extends IService<AstFlowDeploymentEntity> {

    BaseResponse<String> create(AstFlowDeploymentCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowDeploymentUpdateRequestDTO request);

    BaseResponse<AstFlowDeploymentResponseDTO> detail(Long id);

    PageResponse<AstFlowDeploymentResponseDTO> queryPage(BasePageRequest<AstFlowDeploymentQueryRequestDTO> request);
}
