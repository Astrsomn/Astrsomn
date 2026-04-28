package com.astrsomn.server.service;

import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.workflow.core.domain.dto.bizidempotent.AstFlowBizIdempotentCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.bizidempotent.AstFlowBizIdempotentQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.bizidempotent.AstFlowBizIdempotentResponseDTO;
import com.astrsomn.workflow.core.domain.dto.bizidempotent.AstFlowBizIdempotentUpdateRequestDTO;
import com.astrsomn.workflow.core.domain.entity.AstFlowBizIdempotentEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowBizIdempotentService extends IService<AstFlowBizIdempotentEntity> {

    BaseResponse<String> create(AstFlowBizIdempotentCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowBizIdempotentUpdateRequestDTO request);

    BaseResponse<AstFlowBizIdempotentResponseDTO> detail(Long id);

    PageResponse<AstFlowBizIdempotentResponseDTO> queryPage(BasePageRequest<AstFlowBizIdempotentQueryRequestDTO> request);
}
