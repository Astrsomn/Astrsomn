package org.astrsomn.core.common.constant;

/**
 * 向量文档写入 TextSegment Metadata 时使用的键，
 * 与各向量实现 META_DOC_ID_IN_STORE 保持一致。
 */
public final class VecDocMetadataKeys {

    /** 业务侧文档在向量库中的逻辑分组键，用于按文档批量删除等。 */
    public static final String DOC_ID_IN_STORE = "doc_id_in_store";

    private VecDocMetadataKeys() {}
}
