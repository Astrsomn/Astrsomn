package org.astrsomn.starter.langchain.tool.rag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiAccountEntity;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.EmbeddingSetting;
import org.astrsomn.core.common.util.JsonUtil;
import org.astrsomn.core.mapper.AiAccountMapper;
import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiEmbeddingModelFactory {

    private final AiModelMapper aiModelMapper;
    private final AiAccountMapper aiAccountMapper;
    private final AstrsomnProperties astrsomnProperties;

    public <T> EmbeddingModel getEmbeddingModel(AstroChatParam<T> param) {
        EmbeddingSetting embeddingSetting =
                param.getEmbeddingSetting() != null ? param.getEmbeddingSetting() : new EmbeddingSetting();

        AiModelEntity modelEntity = aiModelMapper.selectOne(new LambdaUpdateWrapper<AiModelEntity>()
                .eq(AiModelEntity::getModelKey, param.getModelKey())
                .eq(AiModelEntity::getEnvCode, astrsomnProperties.getEnvCode()));
        if (modelEntity == null) {
            throw new IllegalStateException("未找到嵌入模型: " + param.getModelKey());
        }

        AiModelEnum.ProviderEnum provider = AiModelEnum.ProviderEnum.fromCode(modelEntity.getProvider());
        if (provider == null) {
            throw new IllegalStateException("未找到匹配的模型提供商");
        }

        return switch (provider) {
            case OPENAI, DEEPSEEK, QIANFAN -> buildOpenAiCompatibleEmbedding(modelEntity, embeddingSetting);
            default -> {
                log.warn("嵌入模型 provider {} 未实现，回退 OpenAI 兼容构建: {}", provider, modelEntity.getModelKey());
                yield buildOpenAiCompatibleEmbedding(modelEntity, embeddingSetting);
            }
        };
    }

    private List<String> parseCapabilities(AiModelEntity modelEntity) {
        List<String> list = JsonUtil.parseArray(modelEntity.getCapabilities(), String.class);
        return list != null ? list : Collections.emptyList();
    }

    private AiAccountEntity resolveAccount(AiModelEntity modelEntity) {
        if (StringUtils.isBlank(modelEntity.getAccountKey())) {
            throw new IllegalStateException("模型未配置 accountKey: " + modelEntity.getModelKey());
        }
        AiAccountEntity account = aiAccountMapper.selectOne(new LambdaQueryWrapper<AiAccountEntity>()
                .eq(AiAccountEntity::getAccountKey, modelEntity.getAccountKey()));
        if (account == null || StringUtils.isBlank(account.getApiKey())) {
            throw new IllegalStateException("未找到账号或 API Key: " + modelEntity.getAccountKey());
        }
        return account;
    }

    private EmbeddingModel buildOpenAiCompatibleEmbedding(
            AiModelEntity modelEntity, EmbeddingSetting embeddingSetting) {
        AiAccountEntity account = resolveAccount(modelEntity);
        List<String> caps = parseCapabilities(modelEntity);

        OpenAiEmbeddingModel.OpenAiEmbeddingModelBuilder builder = OpenAiEmbeddingModel.builder()
                .apiKey(account.getApiKey())
                .modelName(modelEntity.getModelName());

        if (StringUtils.isNotBlank(modelEntity.getApiUrl())) {
            builder.baseUrl(modelEntity.getApiUrl());
        }

        applyOpenAiEmbeddingBuilderParams(builder, caps, embeddingSetting);

        return builder.build();
    }

    /**
     * 按 {@link AiModelEnum.EmbeddingInferenceParamEnum} 与 {@link EmbeddingSetting} 应用 LangChain4j Builder 参数。
     */
    private void applyOpenAiEmbeddingBuilderParams(
            OpenAiEmbeddingModel.OpenAiEmbeddingModelBuilder builder,
            List<String> caps,
            EmbeddingSetting es) {
        if (AiModelEnum.EmbeddingInferenceParamEnum.DIMENSIONS.containedIn(caps) && es.getDimensions() != null) {
            builder.dimensions(es.getDimensions());
        }
        if (AiModelEnum.EmbeddingInferenceParamEnum.USER.containedIn(caps) && StringUtils.isNotBlank(es.getUser())) {
            builder.user(es.getUser());
        }
        if (AiModelEnum.EmbeddingInferenceParamEnum.MAX_RETRIES.containedIn(caps) && es.getMaxRetries() != null) {
            builder.maxRetries(es.getMaxRetries());
        }
        if (AiModelEnum.EmbeddingInferenceParamEnum.MAX_SEGMENTS_PER_BATCH.containedIn(caps)
                && es.getMaxSegmentsPerBatch() != null) {
            builder.maxSegmentsPerBatch(es.getMaxSegmentsPerBatch());
        }
        if (AiModelEnum.EmbeddingInferenceParamEnum.ENCODING_FORMAT.containedIn(caps)
                && StringUtils.isNotBlank(es.getEncodingFormat())) {
            builder.encodingFormat(es.getEncodingFormat());
        }
        if (AiModelEnum.EmbeddingInferenceParamEnum.TIMEOUT_SECONDS.containedIn(caps)
                && es.getTimeoutSeconds() != null
                && es.getTimeoutSeconds() > 0) {
            builder.timeout(Duration.ofSeconds(es.getTimeoutSeconds()));
        }
    }
}
