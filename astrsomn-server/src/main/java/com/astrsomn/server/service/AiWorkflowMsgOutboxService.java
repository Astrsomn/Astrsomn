package com.astrsomn.server.service;

import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.workflow.core.domain.dto.msgoutbox.AstFlowMsgOutboxCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.msgoutbox.AstFlowMsgOutboxQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.msgoutbox.AstFlowMsgOutboxResponseDTO;
import com.astrsomn.workflow.core.domain.dto.msgoutbox.AstFlowMsgOutboxUpdateRequestDTO;
import com.astrsomn.workflow.core.domain.entity.AstFlowMsgOutboxEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowMsgOutboxService extends IService<AstFlowMsgOutboxEntity> {

    BaseResponse<String> create(AstFlowMsgOutboxCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowMsgOutboxUpdateRequestDTO request);

    BaseResponse<AstFlowMsgOutboxResponseDTO> detail(Long id);

    PageResponse<AstFlowMsgOutboxResponseDTO> queryPage(BasePageRequest<AstFlowMsgOutboxQueryRequestDTO> request);
}
