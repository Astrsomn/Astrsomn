package org.astrsomn.core.common.dto.conversation;

import lombok.Data;
import org.astrsomn.core.common.entity.AiConversationEntity;

import java.io.Serializable;

@Data
public class AiConversationQueryRequestDTO extends AiConversationEntity implements Serializable {

    private String memoryId;
    private String role;

}
