package com.astrsomn.server.service;

import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.workflow.domain.dto.bizidempotent.AstFlowBizIdempotentCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.bizidempotent.AstFlowBizIdempotentQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.bizidempotent.AstFlowBizIdempotentResponseDTO;
import com.astrsomn.api.workflow.domain.dto.bizidempotent.AstFlowBizIdempotentUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowBizIdempotentEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowBizIdempotentService extends IService<AstFlowBizIdempotentEntity> {

    BaseResponse<String> create(AstFlowBizIdempotentCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowBizIdempotentUpdateRequestDTO request);

    BaseResponse<AstFlowBizIdempotentResponseDTO> detail(Long id);

    PageResponse<AstFlowBizIdempotentResponseDTO> queryPage(BasePageRequest<AstFlowBizIdempotentQueryRequestDTO> request);
}
