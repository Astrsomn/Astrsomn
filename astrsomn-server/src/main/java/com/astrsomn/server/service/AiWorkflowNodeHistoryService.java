package com.astrsomn.server.service;

import com.astrsomn.api.workflow.domain.dto.nodehistory.AstFlowNodeHistoryCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.nodehistory.AstFlowNodeHistoryQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.nodehistory.AstFlowNodeHistoryResponseDTO;
import com.astrsomn.api.workflow.domain.dto.nodehistory.AstFlowNodeHistoryUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowNodeHistoryEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowNodeHistoryService extends IService<AstFlowNodeHistoryEntity> {

    BaseResponse<String> create(AstFlowNodeHistoryCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowNodeHistoryUpdateRequestDTO request);

    BaseResponse<AstFlowNodeHistoryResponseDTO> detail(Long id);

    PageResponse<AstFlowNodeHistoryResponseDTO> queryPage(BasePageRequest<AstFlowNodeHistoryQueryRequestDTO> request);
}
