package com.astrsomn.api.runtime.common.dto.account;

import lombok.Data;

@Data
public class AiAccountUsageStatsDTO {

    private String accountKey;


    private Long callCount;


    private Long promptTokens;


    private Long completionTokens;


    private Long totalTokens;
}