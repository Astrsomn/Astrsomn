package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.server.dto.request.AiAgentCreateRequestDTO;
import org.astrsomn.server.dto.request.AiAgentQueryRequestDTO;
import org.astrsomn.server.dto.request.AiAgentUpdateRequestDTO;
import org.astrsomn.server.dto.response.AiAgentResponseDTO;
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
            String[] idStrs = ids.split(",");
            long[] longIds = new long[idStrs.length];
            for (int i = 0; i < idStrs.length; i++) {
                longIds[i] = Long.parseLong(idStrs[i].trim());
            }
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
    public BaseResponse<AiAgentResponseDTO> detail(@RequestParam("id") String id) {
        try {
            Long longId = Long.parseLong(id);
            return aiAgentService.detail(longId);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }


}
