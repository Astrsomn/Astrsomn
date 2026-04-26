package com.astrsomn.server.service;

import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.workflow.core.domain.dto.instance.AstFlowInstanceCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.instance.AstFlowInstanceQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.instance.AstFlowInstanceResponseDTO;
import com.astrsomn.workflow.core.domain.dto.instance.AstFlowInstanceUpdateRequestDTO;
import com.astrsomn.workflow.core.domain.entity.AstFlowInstanceEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowInstanceService extends IService<AstFlowInstanceEntity> {

    BaseResponse<String> create(AstFlowInstanceCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowInstanceUpdateRequestDTO request);

    BaseResponse<AstFlowInstanceResponseDTO> detail(Long id);

    PageResponse<AstFlowInstanceResponseDTO> queryPage(BasePageRequest<AstFlowInstanceQueryRequestDTO> request);
}
