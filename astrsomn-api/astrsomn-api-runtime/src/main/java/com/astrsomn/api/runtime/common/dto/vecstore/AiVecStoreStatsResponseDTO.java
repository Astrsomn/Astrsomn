package com.astrsomn.api.runtime.common.dto.vecstore;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiVecStoreStatsResponseDTO {

    private Long storeId;

    private Long docCount;

    private Long segmentCount;

    private Long totalWordCount;

    private LocalDateTime lastSyncTime;
}