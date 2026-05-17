package com.astrsomn.server.service.document.chunk;

import com.astrsomn.api.vector.constant.AiVecChunkStrategyEnum;

import java.util.ArrayList;
import java.util.List;

public class ChunkStrategyResolver {

    private static final List<ChunkStrategy> STRATEGIES = new ArrayList<>();

    static {
        STRATEGIES.add(new RecursiveChunkStrategy());
        STRATEGIES.add(new FixedSizeChunkStrategy());
        STRATEGIES.add(new ParagraphChunkStrategy());
        STRATEGIES.add(new SentenceChunkStrategy());
    }

    public static ChunkStrategy resolve(AiVecChunkStrategyEnum strategyEnum) {
        for (ChunkStrategy strategy : STRATEGIES) {
            if (strategy.strategyType() == strategyEnum) {
                return strategy;
            }
        }
        return STRATEGIES.get(0);
    }
}
