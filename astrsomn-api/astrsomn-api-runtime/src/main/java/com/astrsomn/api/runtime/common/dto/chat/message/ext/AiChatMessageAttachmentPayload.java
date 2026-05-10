package com.astrsomn.api.runtime.common.dto.chat.message.ext;

import lombok.Data;

/**
 * 用户消息等多模态附件项。
 */
@Data
public class AiChatMessageAttachmentPayload {

    /** {@link com.astrsomn.api.runtime.common.constant.AiChatEnum.AttachmentKindEnum#getCode()} */
    private String kind;
    private String url;
    private String mimeType;
    private String name;
}
