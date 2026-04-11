package org.astrsomn.vector.chroma.internal;

/**
 * Chroma 与业务层对齐的约定常量（payload / CONFIG_JSON 扩展键）。
 */
public final class ChromaVecConstants {

    /** 写入 {@link dev.langchain4j.data.document.Metadata} 时使用的文档键，用于按文档批量删除。 */
    public static final String META_DOC_ID_IN_STORE = "doc_id_in_store";

    /** LangChain4j Chroma 默认 payload 文本字段名。 */
    public static final String DEFAULT_PAYLOAD_TEXT_KEY = "text_segment";

    private ChromaVecConstants() {}
}