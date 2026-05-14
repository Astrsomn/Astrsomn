package com.astrsomn.server.service;

import com.astrsomn.api.workflow.domain.dto.instance.AstFlowInstanceCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.instance.AstFlowInstanceQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.instance.AstFlowInstanceResponseDTO;
import com.astrsomn.api.workflow.domain.dto.instance.AstFlowInstanceUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowInstanceEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowInstanceService extends IService<AstFlowInstanceEntity> {

    BaseResponse<String> create(AstFlowInstanceCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowInstanceUpdateRequestDTO request);

    BaseResponse<AstFlowInstanceResponseDTO> detail(Long id);

    PageResponse<AstFlowInstanceResponseDTO> queryPage(BasePageRequest<AstFlowInstanceQueryRequestDTO> request);
}
