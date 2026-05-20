package com.astrsomn.server.service.vector.document.chunk;

import com.astrsomn.api.vector.constant.AiVecChunkStrategyEnum;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;

import java.util.List;

public class ParagraphChunkStrategy implements ChunkStrategy {

    @Override
    public AiVecChunkStrategyEnum strategyType() {
        return AiVecChunkStrategyEnum.PARAGRAPH;
    }

    @Override
    public List<TextSegment> split(Document document, int chunkSize, int chunkOverlap) {
        return new DocumentByParagraphSplitter(chunkSize, chunkOverlap).split(document);
    }
}
