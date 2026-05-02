package com.astrsomn.api.runtime.common.dto.conversation;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.AiChatMessageEntity;

import java.io.Serializable;

@Data
public class AiChatMessageQueryRequestDTO extends AiChatMessageEntity implements Serializable {

    private String memoryId;
    private String role;

}
