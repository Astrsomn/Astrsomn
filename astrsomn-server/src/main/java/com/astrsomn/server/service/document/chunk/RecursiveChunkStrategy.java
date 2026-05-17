package com.astrsomn.server.service.document.chunk;

import com.astrsomn.api.vector.constant.AiVecChunkStrategyEnum;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;

import java.util.List;

public class RecursiveChunkStrategy implements ChunkStrategy {

    @Override
    public AiVecChunkStrategyEnum strategyType() {
        return AiVecChunkStrategyEnum.RECURSIVE;
    }

    @Override
    public List<TextSegment> split(Document document, int chunkSize, int chunkOverlap) {
        return DocumentSplitters.recursive(chunkSize, chunkOverlap).split(document);
    }
}
