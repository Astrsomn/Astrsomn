package com.astrsomn.server.api.ai;

import com.astrsomn.api.runtime.common.dto.agent.AiAgentCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentResponseDTO;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.ai.AiAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-agent")
@RequiredArgsConstructor
public class AiAgentController extends BaseController {

    private final AiAgentService aiAgentService;


    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiAgentCreateRequestDTO request) {
        return aiAgentService.create(request);
    }

    @PostMapping("/saveOrUpdate")
    public BaseResponse<String> saveOrUpdate(@RequestBody AiAgentCreateRequestDTO request) {
        return aiAgentService.saveOrUpdate(request);
    }


    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> deleteAgent(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiAgentService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }



    @PostMapping("/queryPage")
    public PageResponse<AiAgentResponseDTO> queryPage(@RequestBody BasePageRequest<AiAgentQueryRequestDTO> request) {
        return aiAgentService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiAgentResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiAgentService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }


}
