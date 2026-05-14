package com.astrsomn.api.runtime.common.dto.chat.message.ext;

import lombok.Data;


@Data
public class AiChatMessageAttachmentPayload {

    
    private String kind;
    private String url;
    private String mimeType;
    private String name;
}