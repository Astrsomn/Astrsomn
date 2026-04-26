package com.astrsomn.server.service;

import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.workflow.core.domain.dto.nodeconfig.AstFlowNodeConfigCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.nodeconfig.AstFlowNodeConfigQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.nodeconfig.AstFlowNodeConfigResponseDTO;
import com.astrsomn.workflow.core.domain.dto.nodeconfig.AstFlowNodeConfigUpdateRequestDTO;
import com.astrsomn.workflow.core.domain.entity.AstFlowNodeConfigEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowNodeConfigService extends IService<AstFlowNodeConfigEntity> {

    BaseResponse<String> create(AstFlowNodeConfigCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowNodeConfigUpdateRequestDTO request);

    BaseResponse<AstFlowNodeConfigResponseDTO> detail(Long id);

    PageResponse<AstFlowNodeConfigResponseDTO> queryPage(BasePageRequest<AstFlowNodeConfigQueryRequestDTO> request);
}
