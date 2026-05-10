package com.astrsomn.api.runtime.common.dto.chat.message.ext;

import lombok.Data;

/**
 * 用户侧组合消息中的有序片段（例如：文本块 + 引用说明）。
 */
@Data
public class AiChatMessagePartPayload {

    private Integer order;
    /** {@link com.astrsomn.api.runtime.common.constant.AiChatEnum.MessagePartKindEnum#getCode()} */
    private String partKind;
    private String content;
}
