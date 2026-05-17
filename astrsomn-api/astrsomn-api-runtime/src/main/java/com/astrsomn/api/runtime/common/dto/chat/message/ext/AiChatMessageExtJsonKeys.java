package com.astrsomn.api.runtime.common.dto.chat.message.ext;


public final class AiChatMessageExtJsonKeys {

    public static final String SCHEMA_VERSION = "schemaVersion";
    public static final String RUN_ID = "runId";

    public static final String SEQ = "seq";

    public static final String LINEAGE = "lineage";

    public static final String PART_KIND = "partKind";
    public static final String TOOL_CALL_ID = "toolCallId";

    public static final String RELATED_TOOL_CALL_ID = "relatedToolCallId";
    public static final String TOOL_NAME = "toolName";
    public static final String ARGUMENTS = "arguments";
    public static final String ATTACHMENTS = "attachments";

    public static final String PARTS = "parts";
    public static final String STREAMING = "streaming";
    public static final String SOURCE = "source";
    public static final String DURATION_MS = "durationMs";

    public static final String RAW_REF = "rawRef";

    public static final String PARENT_MESSAGE_ID = "parentMessageId";

    public static final String PARENT_MESSAGE_ORDER = "parentMessageOrder";

    public static final int SCHEMA_VERSION_V1 = 1;

    private AiChatMessageExtJsonKeys() {
    }
}