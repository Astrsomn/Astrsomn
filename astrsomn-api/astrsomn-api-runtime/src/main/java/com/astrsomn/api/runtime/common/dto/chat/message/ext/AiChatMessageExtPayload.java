package com.astrsomn.api.runtime.common.dto.chat.message.ext;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.Map;


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