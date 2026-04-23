package org.astrsomn.core.common.dto.conversation;

import lombok.Data;

@Data
public class AiConversationUsageDTO {

    private String modelKey;
    
    private Long total;
}
