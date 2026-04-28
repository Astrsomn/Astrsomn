package com.astrsomn.server.service;

import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.workflow.core.domain.dto.timerjob.AstFlowTimerJobCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.timerjob.AstFlowTimerJobQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.timerjob.AstFlowTimerJobResponseDTO;
import com.astrsomn.workflow.core.domain.dto.timerjob.AstFlowTimerJobUpdateRequestDTO;
import com.astrsomn.workflow.core.domain.entity.AstFlowTimerJobEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowTimerJobService extends IService<AstFlowTimerJobEntity> {

    BaseResponse<String> create(AstFlowTimerJobCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowTimerJobUpdateRequestDTO request);

    BaseResponse<AstFlowTimerJobResponseDTO> detail(Long id);

    PageResponse<AstFlowTimerJobResponseDTO> queryPage(BasePageRequest<AstFlowTimerJobQueryRequestDTO> request);
}
