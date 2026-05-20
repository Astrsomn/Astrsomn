package com.astrsomn.server.service.workflow;

import com.astrsomn.api.workflow.domain.dto.timerjob.AstFlowTimerJobCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.timerjob.AstFlowTimerJobQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.timerjob.AstFlowTimerJobResponseDTO;
import com.astrsomn.api.workflow.domain.dto.timerjob.AstFlowTimerJobUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowTimerJobEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowTimerJobService extends IService<AstFlowTimerJobEntity> {

    BaseResponse<String> create(AstFlowTimerJobCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowTimerJobUpdateRequestDTO request);

    BaseResponse<AstFlowTimerJobResponseDTO> detail(Long id);

    PageResponse<AstFlowTimerJobResponseDTO> queryPage(BasePageRequest<AstFlowTimerJobQueryRequestDTO> request);
}
