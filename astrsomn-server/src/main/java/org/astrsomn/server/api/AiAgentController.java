package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.agent.AiAgentCreateRequestDTO;
import org.astrsomn.core.common.dto.agent.AiAgentQueryRequestDTO;
import org.astrsomn.core.common.dto.agent.AiAgentUpdateRequestDTO;
import org.astrsomn.core.common.dto.agent.AiAgentResponseDTO;
import org.astrsomn.server.service.AiAgentService;
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


    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> deleteAgent(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiAgentService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiAgentUpdateRequestDTO request) {
        return aiAgentService.updateAgent(request);
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
