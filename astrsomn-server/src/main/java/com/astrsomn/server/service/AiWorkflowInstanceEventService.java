package com.astrsomn.server.service;

import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.workflow.core.domain.dto.instanceevent.AstFlowInstanceEventCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.instanceevent.AstFlowInstanceEventQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.instanceevent.AstFlowInstanceEventResponseDTO;
import com.astrsomn.workflow.core.domain.dto.instanceevent.AstFlowInstanceEventUpdateRequestDTO;
import com.astrsomn.workflow.core.domain.entity.AstFlowInstanceEventEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowInstanceEventService extends IService<AstFlowInstanceEventEntity> {

    BaseResponse<String> create(AstFlowInstanceEventCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowInstanceEventUpdateRequestDTO request);

    BaseResponse<AstFlowInstanceEventResponseDTO> detail(Long id);

    PageResponse<AstFlowInstanceEventResponseDTO> queryPage(BasePageRequest<AstFlowInstanceEventQueryRequestDTO> request);
}
