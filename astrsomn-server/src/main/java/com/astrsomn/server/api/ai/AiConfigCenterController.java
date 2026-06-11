package com.astrsomn.server.api.ai;

import com.astrsomn.api.runtime.common.dto.configcenter.AiConfigCenterCountsDTO;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.server.service.ai.AiConfigCenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/astro/ai-config-center")
@RequiredArgsConstructor
public class AiConfigCenterController extends BaseController {

    private final AiConfigCenterService aiConfigCenterService;

    @GetMapping("/counts")
    public BaseResponse<AiConfigCenterCountsDTO> counts() {
        return aiConfigCenterService.counts();
    }
}
