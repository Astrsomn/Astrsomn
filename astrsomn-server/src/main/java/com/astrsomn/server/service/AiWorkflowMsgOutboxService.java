package com.astrsomn.server.service;

import com.astrsomn.api.workflow.domain.dto.msgoutbox.AstFlowMsgOutboxCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.msgoutbox.AstFlowMsgOutboxQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.msgoutbox.AstFlowMsgOutboxResponseDTO;
import com.astrsomn.api.workflow.domain.dto.msgoutbox.AstFlowMsgOutboxUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowMsgOutboxEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowMsgOutboxService extends IService<AstFlowMsgOutboxEntity> {

    BaseResponse<String> create(AstFlowMsgOutboxCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowMsgOutboxUpdateRequestDTO request);

    BaseResponse<AstFlowMsgOutboxResponseDTO> detail(Long id);

    PageResponse<AstFlowMsgOutboxResponseDTO> queryPage(BasePageRequest<AstFlowMsgOutboxQueryRequestDTO> request);
}
