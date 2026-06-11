package com.astrsomn.server.service.ai;

import com.astrsomn.api.runtime.common.dto.configcenter.AiConfigCenterCountsDTO;
import com.astrsomn.common.base.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiConfigCenterServiceImpl implements AiConfigCenterService {

    private final AiAccountService aiAccountService;
    private final AiPromptService aiPromptService;
    private final AiMcpService aiMcpService;
    private final AiToolService aiToolService;
    private final AiTemplateService aiTemplateService;
    private final AiChatSessionService aiChatSessionService;

    @Override
    public BaseResponse<AiConfigCenterCountsDTO> counts() {
        AiConfigCenterCountsDTO dto = new AiConfigCenterCountsDTO();

        CompletableFuture<Long> accountFuture = CompletableFuture.supplyAsync(() -> aiAccountService.count());
        CompletableFuture<Long> promptFuture = CompletableFuture.supplyAsync(() -> aiPromptService.count());
        CompletableFuture<Long> mcpFuture = CompletableFuture.supplyAsync(() -> aiMcpService.count());
        CompletableFuture<Long> toolFuture = CompletableFuture.supplyAsync(() -> aiToolService.count());
        CompletableFuture<Long> templateFuture = CompletableFuture.supplyAsync(() -> aiTemplateService.count());
        CompletableFuture<Long> chatSessionFuture = CompletableFuture.supplyAsync(() -> aiChatSessionService.count());

        try {
            CompletableFuture.allOf(accountFuture, promptFuture, mcpFuture, toolFuture, templateFuture, chatSessionFuture).join();
            dto.setAiAccountCount(accountFuture.get());
            dto.setAiPromptCount(promptFuture.get());
            dto.setAiMcpCount(mcpFuture.get());
            dto.setAiToolCount(toolFuture.get());
            dto.setAiTemplateCount(templateFuture.get());
            dto.setAiChatSessionCount(chatSessionFuture.get());
        } catch (Exception e) {
            log.error("Failed to fetch ai-config-center counts", e);
        }

        return BaseResponse.success(dto);
    }
}
