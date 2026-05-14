package com.astrsomn.api.runtime.common.utils;

import com.astrsomn.api.runtime.common.dto.chat.message.AiChatMessageResponseDTO;
import com.astrsomn.api.runtime.common.dto.chat.message.ext.AiChatMessageExtPayload;
import com.astrsomn.api.runtime.common.dto.chat.message.restore.AiChatTurnBundleDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
public final class AiChatMessageRestoreUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private AiChatMessageRestoreUtil() {
    }

    
    public static List<AiChatTurnBundleDTO> bundleByTurn(List<AiChatMessageResponseDTO> rows) {
        if (rows == null || rows.isEmpty()) {
            return List.of();
        }
        List<AiChatMessageResponseDTO> sorted = rows.stream()
                .sorted(Comparator
                        .comparing(AiChatMessageRestoreUtil::turnNoOrZero)
                        .thenComparing(AiChatMessageRestoreUtil::orderOrZero))
                .collect(Collectors.toList());

        Map<Integer, AiChatTurnBundleDTO> map = new LinkedHashMap<>();
        for (AiChatMessageResponseDTO row : sorted) {
            int turn = turnNoOrZero(row);
            map.computeIfAbsent(turn, t -> {
                AiChatTurnBundleDTO b = new AiChatTurnBundleDTO();
                b.setTurnNo(t);
                return b;
            }).getOrderedRows().add(row);
        }
        return new ArrayList<>(map.values());
    }

    public static Optional<AiChatMessageExtPayload> parseExtPayload(String extJson) {
        if (extJson == null || extJson.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(MAPPER.readValue(extJson, AiChatMessageExtPayload.class));
        } catch (Exception e) {
            log.debug("Failed to parse AiChatMessage EXT_JSON: {}", e.getMessage());
            return Optional.empty();
        }
    }

    
    public static Optional<AiChatMessageResponseDTO> findToolCallRow(List<AiChatMessageResponseDTO> turnRows, String toolCallId) {
        if (toolCallId == null || turnRows == null) {
            return Optional.empty();
        }
        for (AiChatMessageResponseDTO row : turnRows) {
            Optional<AiChatMessageExtPayload> ext = parseExtPayload(row.getExtJson());
            if (ext.isPresent() && toolCallId.equals(ext.get().getToolCallId())) {
                return Optional.of(row);
            }
        }
        return Optional.empty();
    }

    private static int turnNoOrZero(AiChatMessageResponseDTO r) {
        return r.getTurnNo() == null ? 0 : r.getTurnNo();
    }

    private static int orderOrZero(AiChatMessageResponseDTO r) {
        return r.getMessageOrder() == null ? 0 : r.getMessageOrder();
    }
}