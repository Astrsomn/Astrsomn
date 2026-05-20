package com.astrsomn.server.service.workflow;

import com.astrsomn.api.workflow.domain.dto.deployment.AstFlowDeploymentCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.deployment.AstFlowDeploymentQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.deployment.AstFlowDeploymentResponseDTO;
import com.astrsomn.api.workflow.domain.dto.deployment.AstFlowDeploymentUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowDeploymentEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowDeploymentService extends IService<AstFlowDeploymentEntity> {

    BaseResponse<String> create(AstFlowDeploymentCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowDeploymentUpdateRequestDTO request);

    BaseResponse<AstFlowDeploymentResponseDTO> detail(Long id);

    PageResponse<AstFlowDeploymentResponseDTO> queryPage(BasePageRequest<AstFlowDeploymentQueryRequestDTO> request);
}
