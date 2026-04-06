package org.astrsomn.vector.qdrant;

import org.astrsomn.core.common.entity.AiVecDocEntity;
import org.astrsomn.core.common.entity.AiVecSegmentEntity;
import org.astrsomn.core.common.langchain.vector.AbstractVecDoc;
import org.astrsomn.core.common.langchain.vector.AbstractVecSegment;

/**
 * 文档级删除依赖向量点 payload 中的 {@link #DEFAULT_DOC_PAYLOAD_KEY}，与业务入库时写入的 metadata 一致。
 */
public class QdrantVecDoc extends AbstractVecDoc {

    /**
     * 默认与 {@link dev.langchain4j.data.segment.TextSegment#metadata()} 中 key 对齐；入库时需写入 {@code doc_id = AiVecDocEntity.id}。
     */
    public static final String DEFAULT_DOC_PAYLOAD_KEY = "doc_id";

    private final String docPayloadKey;

    public QdrantVecDoc(QdrantVecStore store, AiVecDocEntity entity) {
        this(store, entity, DEFAULT_DOC_PAYLOAD_KEY);
    }

    public QdrantVecDoc(QdrantVecStore store, AiVecDocEntity entity, String docPayloadKey) {
        super(store, entity);
        this.docPayloadKey = docPayloadKey == null ? DEFAULT_DOC_PAYLOAD_KEY : docPayloadKey;
    }

    @Override
    public void deleteAllEmbeddingsInStore() {
        AiVecDocEntity doc = getEntity();
        if (doc.getId() == null) {
            return;
        }
        QdrantVecStore store = (QdrantVecStore) getStore();
        String collection = store.getEntity().getCollectionName();
        store.adminClient().deletePointsByPayloadMatch(collection, docPayloadKey, String.valueOf(doc.getId()));
    }

    @Override
    public AbstractVecSegment bindSegment(AiVecSegmentEntity segment) {
        return new QdrantVecSegment(this, segment);
    }
}
