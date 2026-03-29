package org.astrsomn.starter.langchain.tool.rag;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.mapper.AiModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiEmbeddingModelFactory {


    private final AiModelMapper aiModelMapper;

    public <T> EmbeddingModel getEmbeddingModel(AstroChatParam<T> param) {
        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey("your-openai-api-key")
                .modelName("text-embedding-3-small") // 模型名称
                .dimensions(10)

                .build();

        return null;
    }
}
