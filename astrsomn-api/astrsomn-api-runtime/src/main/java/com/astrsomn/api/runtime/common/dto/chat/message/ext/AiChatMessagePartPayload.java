package com.astrsomn.api.runtime.common.dto.chat.message.ext;

import lombok.Data;


@Data
public class AiChatMessagePartPayload {

    private Integer order;

    private String partKind;
    private String content;
}