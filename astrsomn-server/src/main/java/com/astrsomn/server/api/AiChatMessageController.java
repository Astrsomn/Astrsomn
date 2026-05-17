package com.astrsomn.server.api;

import com.astrsomn.api.runtime.common.dto.chat.message.AiChatMessageCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.chat.message.AiChatMessageQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.chat.message.AiChatMessageResponseDTO;
import com.astrsomn.api.runtime.common.dto.chat.message.AiChatMessageUpdateRequestDTO;
import com.astrsomn.api.runtime.common.dto.chat.message.restore.AiChatTurnBundleDTO;
import com.astrsomn.common.base.BaseController;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.service.AiChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/astro/ai-chat-message")
@RequiredArgsConstructor
public class AiChatMessageController extends BaseController {

    private final AiChatMessageService aiChatMessageService;

    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody AiChatMessageCreateRequestDTO request) {
        return aiChatMessageService.create(request);
    }

    @DeleteMapping("/delete/{ids}")
    public BaseResponse<String> delete(@PathVariable("ids") String ids) {
        try {
            long[] longIds = parseLongIds(ids, ",");
            return aiChatMessageService.delete(longIds);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody AiChatMessageUpdateRequestDTO request) {
        return aiChatMessageService.update(request);
    }

    @PostMapping("/queryPage")
    public PageResponse<AiChatMessageResponseDTO> queryPage(@RequestBody BasePageRequest<AiChatMessageQueryRequestDTO> request) {
        return aiChatMessageService.queryPage(request);
    }

    @GetMapping("/detail")
    public BaseResponse<AiChatMessageResponseDTO> detail(@RequestParam("id") Long id) {
        try {
            return aiChatMessageService.detail(id);
        } catch (NumberFormatException e) {
            return BaseResponse.fail("ID格式错误", null);
        }
    }


    @GetMapping("/recoverByMemoryKey")
    public BaseResponse<List<AiChatMessageResponseDTO>> recoverByMemoryKey(@RequestParam("memoryKey") String memoryKey) {
        return aiChatMessageService.recoverByMemoryKey(memoryKey);
    }

    @GetMapping("/recoverTurnsByMemoryKey")
    public BaseResponse<List<AiChatTurnBundleDTO>> recoverTurnsByMemoryKey(@RequestParam("memoryKey") String memoryKey) {
        return aiChatMessageService.recoverTurnsByMemoryKey(memoryKey);
    }
}
