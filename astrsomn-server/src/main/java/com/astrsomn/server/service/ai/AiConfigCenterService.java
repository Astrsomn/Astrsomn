package com.astrsomn.server.service.ai;

import com.astrsomn.api.runtime.common.dto.configcenter.AiConfigCenterCountsDTO;
import com.astrsomn.common.base.BaseResponse;

public interface AiConfigCenterService {

    BaseResponse<AiConfigCenterCountsDTO> counts();
}
