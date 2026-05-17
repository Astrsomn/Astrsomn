package com.astrsomn.server.service.document.chunk;

import com.astrsomn.api.vector.constant.AiVecChunkStrategyEnum;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;

import java.util.List;

public interface ChunkStrategy {

    AiVecChunkStrategyEnum strategyType();

    List<TextSegment> split(Document document, int chunkSize, int chunkOverlap);
}
