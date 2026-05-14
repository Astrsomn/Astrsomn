package com.astrsomn.server.service;

import com.astrsomn.api.workflow.domain.dto.instanceevent.AstFlowInstanceEventCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.instanceevent.AstFlowInstanceEventQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.instanceevent.AstFlowInstanceEventResponseDTO;
import com.astrsomn.api.workflow.domain.dto.instanceevent.AstFlowInstanceEventUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowInstanceEventEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowInstanceEventService extends IService<AstFlowInstanceEventEntity> {

    BaseResponse<String> create(AstFlowInstanceEventCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowInstanceEventUpdateRequestDTO request);

    BaseResponse<AstFlowInstanceEventResponseDTO> detail(Long id);

    PageResponse<AstFlowInstanceEventResponseDTO> queryPage(BasePageRequest<AstFlowInstanceEventQueryRequestDTO> request);
}
