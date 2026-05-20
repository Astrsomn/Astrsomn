package com.astrsomn.server.service.workflow;

import com.astrsomn.api.workflow.domain.dto.humantask.AstFlowHumanTaskCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.humantask.AstFlowHumanTaskQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.humantask.AstFlowHumanTaskResponseDTO;
import com.astrsomn.api.workflow.domain.dto.humantask.AstFlowHumanTaskUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowHumanTaskEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowHumanTaskService extends IService<AstFlowHumanTaskEntity> {

    BaseResponse<String> create(AstFlowHumanTaskCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowHumanTaskUpdateRequestDTO request);

    BaseResponse<AstFlowHumanTaskResponseDTO> detail(Long id);

    PageResponse<AstFlowHumanTaskResponseDTO> queryPage(BasePageRequest<AstFlowHumanTaskQueryRequestDTO> request);
}
