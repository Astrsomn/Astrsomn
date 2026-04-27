package com.astrsomn.core.common.dto.conversation;

import lombok.Data;
import com.astrsomn.core.common.entity.AiChatMessageEntity;

import java.io.Serializable;

@Data
public class AiChatMessageQueryRequestDTO extends AiChatMessageEntity implements Serializable {

    private String memoryId;
    private String role;

}
