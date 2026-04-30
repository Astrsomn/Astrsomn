package com.astrsomn.server.service;

import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.workflow.core.domain.dto.nodehistory.AstFlowNodeHistoryCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.nodehistory.AstFlowNodeHistoryQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.nodehistory.AstFlowNodeHistoryResponseDTO;
import com.astrsomn.workflow.core.domain.dto.nodehistory.AstFlowNodeHistoryUpdateRequestDTO;
import com.astrsomn.workflow.core.domain.entity.AstFlowNodeHistoryEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowNodeHistoryService extends IService<AstFlowNodeHistoryEntity> {

    BaseResponse<String> create(AstFlowNodeHistoryCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowNodeHistoryUpdateRequestDTO request);

    BaseResponse<AstFlowNodeHistoryResponseDTO> detail(Long id);

    PageResponse<AstFlowNodeHistoryResponseDTO> queryPage(BasePageRequest<AstFlowNodeHistoryQueryRequestDTO> request);
}
