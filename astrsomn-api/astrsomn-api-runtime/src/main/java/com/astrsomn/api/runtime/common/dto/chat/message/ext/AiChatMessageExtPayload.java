package com.astrsomn.api.runtime.common.dto.chat.message.ext;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * AI_CHAT_MESSAGE.EXT_JSON 的 v1 结构；与流式事件、前端 segment 对齐。
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiChatMessageExtPayload {

    private Integer schemaVersion;
    private String runId;
    private Integer seq;
    private String lineage;
    private String partKind;
    private String toolCallId;
    private String relatedToolCallId;
    private String toolName;
    private Map<String, Object> arguments;
    private List<AiChatMessageAttachmentPayload> attachments;
    private List<AiChatMessagePartPayload> parts;
    private Boolean streaming;
    private String source;
    private Long durationMs;
    private String rawRef;
    private Long parentMessageId;
    private Integer parentMessageOrder;
}
