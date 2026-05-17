package com.astrsomn.api.runtime.common.dto.chat.message.restore;

import com.astrsomn.api.runtime.common.dto.chat.message.AiChatMessageResponseDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Data
public class AiChatTurnBundleDTO {

    private Integer turnNo;
    private List<AiChatMessageResponseDTO> orderedRows = new ArrayList<>();

    public Optional<AiChatMessageResponseDTO> getUserRoot() {
        return orderedRows.stream()
                .filter(r -> "user".equalsIgnoreCase(r.getRole()))
                .findFirst();
    }


    public List<AiChatMessageResponseDTO> getReplyChain() {
        return orderedRows.stream()
                .filter(r -> !"user".equalsIgnoreCase(r.getRole()))
                .collect(Collectors.toList());
    }
}