package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.tracelog.AiTraceLogCreateRequestDTO;
import com.astrsomn.core.common.dto.tracelog.AiTraceLogQueryRequestDTO;
import com.astrsomn.core.common.dto.tracelog.AiTraceLogResponseDTO;
import com.astrsomn.core.common.dto.tracelog.AiTraceLogUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiTraceLogEntity;

public interface AiTraceLogService extends IService<AiTraceLogEntity> {

    BaseResponse<String> create(AiTraceLogCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiTraceLogUpdateRequestDTO request);

    PageResponse<AiTraceLogResponseDTO> queryPage(BasePageRequest<AiTraceLogQueryRequestDTO> request);

    BaseResponse<AiTraceLogResponseDTO> detail(Long id);
}
