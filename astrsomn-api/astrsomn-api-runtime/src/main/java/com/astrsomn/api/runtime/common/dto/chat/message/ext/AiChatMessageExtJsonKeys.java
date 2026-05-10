package com.astrsomn.api.runtime.common.dto.chat.message.ext;

/**
 * {@link com.astrsomn.api.runtime.common.entity.AiChatMessageEntity#getExtJson()} 约定字段名（schema v1）。
 */
public final class AiChatMessageExtJsonKeys {

    public static final String SCHEMA_VERSION = "schemaVersion";
    public static final String RUN_ID = "runId";
    /** 同一次模型响应内片段序号（可选，与 MESSAGE_ORDER 互补）。 */
    public static final String SEQ = "seq";
    /** {@link com.astrsomn.api.runtime.common.constant.AiChatEnum.MessageLineageEnum#getCode()} */
    public static final String LINEAGE = "lineage";
    /** {@link com.astrsomn.api.runtime.common.constant.AiChatEnum.MessagePartKindEnum#getCode()}，与 SSE type 对齐。 */
    public static final String PART_KIND = "partKind";
    public static final String TOOL_CALL_ID = "toolCallId";
    /** 工具结果行指向对应 tool_call 的 id。 */
    public static final String RELATED_TOOL_CALL_ID = "relatedToolCallId";
    public static final String TOOL_NAME = "toolName";
    public static final String ARGUMENTS = "arguments";
    public static final String ATTACHMENTS = "attachments";
    /** 用户多模态：有序片段（文本+引用等）。 */
    public static final String PARTS = "parts";
    public static final String STREAMING = "streaming";
    public static final String SOURCE = "source";
    public static final String DURATION_MS = "durationMs";
    /** 大结果外存指针（URL / 业务主键）。 */
    public static final String RAW_REF = "rawRef";
    /** 落库后父行 ID（可选，用于强关联）。 */
    public static final String PARENT_MESSAGE_ID = "parentMessageId";
    /**
     * 同一轮次内软锚点：指向兄弟行的 {@code MESSAGE_ORDER}（插入前占位）。
     */
    public static final String PARENT_MESSAGE_ORDER = "parentMessageOrder";

    public static final int SCHEMA_VERSION_V1 = 1;

    private AiChatMessageExtJsonKeys() {
    }
}
