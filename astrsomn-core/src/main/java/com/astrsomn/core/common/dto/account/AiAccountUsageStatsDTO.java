package com.astrsomn.core.common.dto.account;

import lombok.Data;

@Data
public class AiAccountUsageStatsDTO {

    private String accountKey;

    /**
     * 调用次数（消息条数）。
     */
    private Long callCount;

    /**
     * 输入 token 累计。
     */
    private Long promptTokens;

    /**
     * 输出 token 累计。
     */
    private Long completionTokens;

    /**
     * 总 token 累计。
     */
    private Long totalTokens;
}
