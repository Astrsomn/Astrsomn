package com.astrsomn.vector.milvus.internal;

/**
 * Milvus 与业务层对齐的元数据键（与 LangChain4j Milvus 默认 metadata 字段一致）。
 */
public final class MilvusVecConstants {

    /** 写入 {@link dev.langchain4j.data.document.Metadata} 时使用的文档键，用于按文档批量删除。 */
    public static final String META_DOC_ID_IN_STORE = "doc_id_in_store";

    private MilvusVecConstants() {}
}
