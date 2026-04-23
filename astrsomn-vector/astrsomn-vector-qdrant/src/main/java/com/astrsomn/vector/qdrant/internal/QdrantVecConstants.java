package com.astrsomn.vector.qdrant.internal;

/**
 * Qdrant 与业务层对齐的约定常量（payload / CONFIG_JSON 扩展键）。
 */
public final class QdrantVecConstants {

    /** 写入 {@link dev.langchain4j.data.document.Metadata} 时使用的文档键，用于按文档批量删除。 */
    public static final String META_DOC_ID_IN_STORE = "doc_id_in_store";

    /** {@code CONFIG_JSON} 布尔：是否对 gRPC 使用 TLS。 */
    public static final String CONFIG_USE_TLS = "useTls";

    /**
     * {@code CONFIG_JSON} 布尔：是否在客户端初始化时与 Qdrant 校验版本（会多一次 RPC；网络不可达时会长时间阻塞）。
     * 缺省按 {@link QdrantConfigSupport#readCheckCompatibility} 处理。
     */
    public static final String CONFIG_CHECK_COMPATIBILITY = "checkCompatibility";

    /** LangChain4j Qdrant 默认 payload 文本字段名。 */
    public static final String DEFAULT_PAYLOAD_TEXT_KEY = "text_segment";

    private QdrantVecConstants() {}
}
