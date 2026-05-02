package com.astrsomn.server.service;

import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.workflow.domain.dto.nodeconfig.AstFlowNodeConfigCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.nodeconfig.AstFlowNodeConfigQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.nodeconfig.AstFlowNodeConfigResponseDTO;
import com.astrsomn.api.workflow.domain.dto.nodeconfig.AstFlowNodeConfigUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowNodeConfigEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiWorkflowNodeConfigService extends IService<AstFlowNodeConfigEntity> {

    BaseResponse<String> create(AstFlowNodeConfigCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AstFlowNodeConfigUpdateRequestDTO request);

    BaseResponse<AstFlowNodeConfigResponseDTO> detail(Long id);

    PageResponse<AstFlowNodeConfigResponseDTO> queryPage(BasePageRequest<AstFlowNodeConfigQueryRequestDTO> request);
}
