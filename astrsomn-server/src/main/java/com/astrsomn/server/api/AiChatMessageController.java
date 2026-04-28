package com.astrsomn.server.api;

import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.base.BaseController;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.chat.message.AiChatMessageCreateRequestDTO;
import com.astrsomn.core.common.dto.chat.message.AiChatMessageQueryRequestDTO;
import com.astrsomn.core.common.dto.chat.message.AiChatMessageResponseDTO;
import com.astrsomn.core.common.dto.chat.message.AiChatMessageUpdateRequestDTO;
import com.astrsomn.server.service.AiChatMessageService;
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
}
