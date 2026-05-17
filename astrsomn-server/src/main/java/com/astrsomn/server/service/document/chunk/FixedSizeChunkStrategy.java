package com.astrsomn.server.service.document.chunk;

import com.astrsomn.api.vector.constant.AiVecChunkStrategyEnum;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.segment.TextSegment;

import java.util.List;

public class FixedSizeChunkStrategy implements ChunkStrategy {

    @Override
    public AiVecChunkStrategyEnum strategyType() {
        return AiVecChunkStrategyEnum.FIXED_SIZE;
    }

    @Override
    public List<TextSegment> split(Document document, int chunkSize, int chunkOverlap) {
        return new DocumentByCharacterSplitter(chunkSize, chunkOverlap).split(document);
    }
}
