package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.dto.tracelog.AiTraceLogCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.tracelog.AiTraceLogQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.tracelog.AiTraceLogResponseDTO;
import com.astrsomn.api.runtime.common.dto.tracelog.AiTraceLogUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiTraceLogEntity;

public interface AiTraceLogService extends IService<AiTraceLogEntity> {

    BaseResponse<String> create(AiTraceLogCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiTraceLogUpdateRequestDTO request);

    PageResponse<AiTraceLogResponseDTO> queryPage(BasePageRequest<AiTraceLogQueryRequestDTO> request);

    BaseResponse<AiTraceLogResponseDTO> detail(Long id);
}
