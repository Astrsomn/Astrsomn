package com.astrsomn.server.service;

import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.workflow.core.domain.dto.humantask.AstFlowHumanTaskCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.humantask.AstFlowHumanTaskQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.humantask.AstFlowHumanTaskResponseDTO;
import com.astrsomn.workflow.core.domain.dto.humantask.AstFlowHumanTaskUpdateRequestDTO;
import com.astrsomn.workflow.core.domain.entity.AstFlowHumanTaskEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowHumanTaskService extends IService<AstFlowHumanTaskEntity> {

    BaseResponse<String> create(AstFlowHumanTaskCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowHumanTaskUpdateRequestDTO request);

    BaseResponse<AstFlowHumanTaskResponseDTO> detail(Long id);

    PageResponse<AstFlowHumanTaskResponseDTO> queryPage(BasePageRequest<AstFlowHumanTaskQueryRequestDTO> request);
}
