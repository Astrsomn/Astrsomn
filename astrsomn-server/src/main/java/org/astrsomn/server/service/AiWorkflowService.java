package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.workflow.*;
import org.astrsomn.core.common.entity.AiWorkflowEntity;

public interface AiWorkflowService extends IService<AiWorkflowEntity> {

    BaseResponse<String> create(AiWorkflowCreateRequestDTO request);

    BaseResponse<String> update(AiWorkflowUpdateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiWorkflowResponseDTO> detail(Long id);

    PageResponse<AiWorkflowResponseDTO> queryPage(BasePageRequest<AiWorkflowQueryRequestDTO> request);

    BaseResponse<String> publish(Long id);

    BaseResponse<AiWorkflowTestRunResponseDTO> testRun(AiWorkflowTestRunRequestDTO request);
}
