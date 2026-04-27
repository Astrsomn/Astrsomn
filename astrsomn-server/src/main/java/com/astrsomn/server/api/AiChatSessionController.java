package com.astrsomn.server.api;

import com.astrsomn.commn.base.BaseController;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.chat.session.AiChatSessionCreateRequestDTO;
import com.astrsomn.core.common.dto.chat.session.AiChatSessionQueryRequestDTO;
import com.astrsomn.core.common.dto.chat.session.AiChatSessionResponseDTO;
import com.astrsomn.core.common.dto.chat.session.AiChatSessionUpdateRequestDTO;
import com.astrsomn.server.service.AiChatSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-chat-session")
@RequiredArgsConstructor
public class AiChatSessionController extends BaseController {

    private final AiChatSessionService aiChatSessionService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiChatSessionCreateRequestDTO request) {
        return aiChatSessionService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiChatSessionService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiChatSessionUpdateRequestDTO request) {
        return aiChatSessionService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiChatSessionResponseDTO> queryPage(
            @RequestBody BasePageRequest<AiChatSessionQueryRequestDTO> request) {
        return aiChatSessionService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiChatSessionResponseDTO> detail(@RequestParam("id") Long id) {
        return aiChatSessionService.detail(id);
    }
}
