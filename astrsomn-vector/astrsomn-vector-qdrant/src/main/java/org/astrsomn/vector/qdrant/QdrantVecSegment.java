package org.astrsomn.vector.qdrant;

import org.astrsomn.core.common.langchain.vector.AbstractVecSegment;
import org.astrsomn.core.common.util.StringUtils;

import java.util.List;

public class QdrantVecSegment extends AbstractVecSegment {

    public QdrantVecSegment(QdrantVecDoc doc, org.astrsomn.core.common.entity.AiVecSegmentEntity entity) {
        super(doc, entity);
    }

    @Override
    public void deleteEmbedding() {
        String vectorId = getEntity().getVectorId();
        if (StringUtils.isEmpty(vectorId)) {
            return;
        }
        QdrantVecDoc qDoc = (QdrantVecDoc) getDoc();
        QdrantVecStore store = (QdrantVecStore) qDoc.getStore();
        String collection = store.getEntity().getCollectionName();
        store.adminClient().deletePointsByIds(collection, List.of(vectorId.trim()));
    }
}
