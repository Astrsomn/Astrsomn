package com.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.base.BaseController;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.conversation.AiConversationCreateRequestDTO;
import com.astrsomn.core.common.dto.conversation.AiConversationQueryRequestDTO;
import com.astrsomn.core.common.dto.conversation.AiConversationResponseDTO;
import com.astrsomn.core.common.dto.conversation.AiConversationUpdateRequestDTO;
import com.astrsomn.server.service.AiConversationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/queryGroups")
    public PageResponse<AiConversationResponseDTO> queryGroups(@RequestBody BasePageRequest<AiConversationQueryRequestDTO> request) {
        return aiConversationService.queryGroups(request);
    }

    @GetMapping("/recoverByMemoryKey")
    public BaseResponse<List<AiConversationResponseDTO>> recoverByMemoryKey(@RequestParam("memoryKey") String memoryKey) {
        return aiConversationService.recoverByMemoryKey(memoryKey);
    }
}
