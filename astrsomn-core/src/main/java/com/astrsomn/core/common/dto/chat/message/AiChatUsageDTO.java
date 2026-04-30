package com.astrsomn.core.common.dto.chat.message;

import lombok.Data;

@Data
public class AiChatUsageDTO {

    private String modelKey;

    private Long total;
}
