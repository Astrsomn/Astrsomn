package com.astrsomn.vector.chroma.internal;

/**
 * Chroma 与业务层对齐的约定常量（payload / CONFIG_JSON 扩展键）。
 */
public final class ChromaVecConstants {

    /** 写入 {@link dev.langchain4j.data.document.Metadata} 时使用的文档键，用于按文档批量删除。 */
    public static final String META_DOC_ID_IN_STORE = "doc_id_in_store";

    /** LangChain4j Chroma 默认 payload 文本字段名。 */
    public static final String DEFAULT_PAYLOAD_TEXT_KEY = "text_segment";

    /** {@code CONFIG_JSON} 字符串：Chroma REST API 版本，取值 {@code V1} / {@code V2}；缺省为 V2（适配 Chroma 0.7+）。 */
    public static final String CONFIG_API_VERSION = "apiVersion";

    /** {@code CONFIG_JSON} 字符串：Chroma API V2 租户名；缺省由 LangChain4j 使用 {@code default}。 */
    public static final String CONFIG_TENANT_NAME = "tenantName";

    /** {@code CONFIG_JSON} 布尔：是否对 HTTP 使用 TLS（{@code https://}）。 */
    public static final String CONFIG_USE_TLS = "useTls";

    private ChromaVecConstants() {}
}