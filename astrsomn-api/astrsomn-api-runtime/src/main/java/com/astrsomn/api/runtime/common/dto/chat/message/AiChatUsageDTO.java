package com.astrsomn.api.runtime.common.dto.chat.message;

import lombok.Data;

@Data
public class AiChatUsageDTO {

    private String modelKey;

    private Long total;
}
