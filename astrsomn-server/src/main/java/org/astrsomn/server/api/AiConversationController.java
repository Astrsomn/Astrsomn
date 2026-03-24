package org.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseController;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.conversation.AiConversationCreateRequestDTO;
import org.astrsomn.core.common.dto.conversation.AiConversationQueryRequestDTO;
import org.astrsomn.core.common.dto.conversation.AiConversationUpdateRequestDTO;
import org.astrsomn.core.common.dto.conversation.AiConversationResponseDTO;
import org.astrsomn.server.service.AiConversationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/astro/ai-conversation")
@RequiredArgsConstructor
public class AiConversationController extends BaseController {

    private final AiConversationService aiConversationService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiConversationCreateRequestDTO request) {
        return aiConversationService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiConversationService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiConversationUpdateRequestDTO request) {
        return aiConversationService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiConversationResponseDTO> queryPage(@RequestBody BasePageRequest<AiConversationQueryRequestDTO> request) {
        return aiConversationService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiConversationResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiConversationService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }
}
