package com.astrsomn.api.runtime.common.dto.chat.message.restore;

import com.astrsomn.api.runtime.common.dto.chat.message.AiChatMessageResponseDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 单个对话轮次（同一 {@code TURN_NO}）下的全部库表行，已按 {@code MESSAGE_ORDER} 排序，便于复原上下文与子消息链。
 */
@Data
public class AiChatTurnBundleDTO {

    private Integer turnNo;
    private List<AiChatMessageResponseDTO> orderedRows = new ArrayList<>();

    public Optional<AiChatMessageResponseDTO> getUserRoot() {
        return orderedRows.stream()
                .filter(r -> "user".equalsIgnoreCase(r.getRole()))
                .findFirst();
    }

    /**
     * 助手、思考、工具等回复链（不含 user 根行）。
     */
    public List<AiChatMessageResponseDTO> getReplyChain() {
        return orderedRows.stream()
                .filter(r -> !"user".equalsIgnoreCase(r.getRole()))
                .collect(Collectors.toList());
    }
}
