package org.astrsomn.starter.langchain.tool.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.springframework.stereotype.Component;

@Component
public class VectorStoreRegistry {
    public EmbeddingStore<TextSegment> getStore(AstroChatParam param) {
        return null;
    }
}
