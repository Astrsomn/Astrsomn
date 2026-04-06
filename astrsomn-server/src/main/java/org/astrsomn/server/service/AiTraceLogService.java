package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.tracelog.AiTraceLogCreateRequestDTO;
import org.astrsomn.core.common.dto.tracelog.AiTraceLogQueryRequestDTO;
import org.astrsomn.core.common.dto.tracelog.AiTraceLogResponseDTO;
import org.astrsomn.core.common.dto.tracelog.AiTraceLogUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiTraceLogEntity;
import org.astrsomn.core.mapper.AiTraceLogMapper;

public interface AiTraceLogService extends IService<AiTraceLogEntity> {

    BaseResponse<String> create(AiTraceLogCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiTraceLogUpdateRequestDTO request);

    PageResponse<AiTraceLogResponseDTO> queryPage(BasePageRequest<AiTraceLogQueryRequestDTO> request);

    BaseResponse<AiTraceLogResponseDTO> detail(Long id);
}
