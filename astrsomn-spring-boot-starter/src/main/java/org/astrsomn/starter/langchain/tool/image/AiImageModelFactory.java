package org.astrsomn.starter.langchain.tool.image;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiAccountEntity;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.ConversationSetting;
import org.astrsomn.core.common.langchain.buildParam.setting.ImageSetting;
import org.astrsomn.core.common.langchain.buildParam.setting.ChatSetting;
import org.astrsomn.core.common.util.JsonUtil;
import org.astrsomn.core.mapper.AiAccountMapper;
import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiImageModelFactory {

    private final AiModelMapper aiModelMapper;
    private final AiAccountMapper aiAccountMapper;
    private final AstrsomnProperties astrsomnProperties;

    public <T> ImageModel getImageModel(AstroChatParam<T> param) {
        ChatSetting chatSetting = param.getChatSetting();
        ConversationSetting conversationSetting = param.getConversationSetting();
        ImageSetting imageSetting = param.getImageSetting() != null ? param.getImageSetting() : new ImageSetting();

        AiModelEntity modelEntity = aiModelMapper.selectOne(new LambdaUpdateWrapper<AiModelEntity>()
                .eq(AiModelEntity::getModelKey, param.getModelKey())
                .eq(AiModelEntity::getEnvCode, astrsomnProperties.getEnvCode()));
        if (modelEntity == null) {
            throw new IllegalStateException("未找到图像模型: " + param.getModelKey());
        }

        AiModelEnum.ProviderEnum providerEnum = AiModelEnum.ProviderEnum.fromCode(modelEntity.getProvider());
        if (Objects.isNull(providerEnum)) {
            log.error("====>  Astrsomn  ====>  未找到匹配的模型提供商");
            throw new IllegalStateException("未找到匹配的模型提供商");
        }

        return switch (providerEnum) {
            case ALIBABA -> getQwenImageModel(modelEntity, chatSetting, conversationSetting, imageSetting);
            case OPENAI -> getOpenAiImageModel(modelEntity, chatSetting, conversationSetting, imageSetting);
            case ZHIPU -> getZhiPuImageModel(modelEntity, chatSetting, conversationSetting, imageSetting);
            case QIANFAN -> getQianfanImageModel(modelEntity, chatSetting, conversationSetting, imageSetting);
            case GOOGLE -> getGoogleGeminiImageModel(modelEntity, chatSetting, conversationSetting, imageSetting);
            default -> {
                log.error("====>  Astrsomn  ====> 未处理的 provider 类型: {} <====", providerEnum);
                throw new IllegalStateException("未知的大模型参数");
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

    private ImageModel getGoogleGeminiImageModel(
            AiModelEntity modelEntity, ChatSetting chatSetting, ConversationSetting conversationSetting, ImageSetting imageSetting) {
        return null;
    }

    private ImageModel getQianfanImageModel(
            AiModelEntity modelEntity, ChatSetting chatSetting, ConversationSetting conversationSetting, ImageSetting imageSetting) {
        return null;
    }

    private ImageModel getZhiPuImageModel(
            AiModelEntity modelEntity, ChatSetting chatSetting, ConversationSetting conversationSetting, ImageSetting imageSetting) {
        return null;
    }

    private ImageModel getOpenAiImageModel(
            AiModelEntity modelEntity, ChatSetting chatSetting, ConversationSetting conversationSetting, ImageSetting imageSetting) {
        AiAccountEntity account = resolveAccount(modelEntity);
        List<String> caps = parseCapabilities(modelEntity);

        OpenAiImageModel.OpenAiImageModelBuilder builder = OpenAiImageModel.builder()
                .apiKey(account.getApiKey())
                .modelName(modelEntity.getModelName());

        if (StringUtils.isNotBlank(modelEntity.getApiUrl())) {
            builder.baseUrl(modelEntity.getApiUrl());
        }

        applyOpenAiImageBuilderParams(builder, caps, imageSetting);

        return builder.build();
    }

    /**
     * 按 {@link AiModelEnum.ImageGenParamEnum} 与 {@link ImageSetting} 应用 LangChain4j OpenAI 图像 Builder 参数。
     */
    private void applyOpenAiImageBuilderParams(
            OpenAiImageModel.OpenAiImageModelBuilder builder,
            List<String> caps,
            ImageSetting img) {
        if (AiModelEnum.ImageGenParamEnum.SIZE.containedIn(caps) && StringUtils.isNotBlank(img.getSize())) {
            builder.size(img.getSize());
        }
        if (AiModelEnum.ImageGenParamEnum.QUALITY.containedIn(caps) && StringUtils.isNotBlank(img.getQuality())) {
            builder.quality(img.getQuality());
        }
        if (AiModelEnum.ImageGenParamEnum.STYLE.containedIn(caps) && StringUtils.isNotBlank(img.getStyle())) {
            builder.style(img.getStyle());
        }
        if (AiModelEnum.ImageGenParamEnum.USER.containedIn(caps) && StringUtils.isNotBlank(img.getUser())) {
            builder.user(img.getUser());
        }
        if (AiModelEnum.ImageGenParamEnum.RESPONSE_FORMAT.containedIn(caps)
                && StringUtils.isNotBlank(img.getResponseFormat())) {
            builder.responseFormat(img.getResponseFormat());
        }
        if (AiModelEnum.ImageGenParamEnum.MAX_RETRIES.containedIn(caps) && img.getMaxRetries() != null) {
            builder.maxRetries(img.getMaxRetries());
        }
        if (AiModelEnum.ImageGenParamEnum.TIMEOUT_SECONDS.containedIn(caps)
                && img.getTimeoutSeconds() != null
                && img.getTimeoutSeconds() > 0) {
            builder.timeout(Duration.ofSeconds(img.getTimeoutSeconds()));
        }
    }

    private ImageModel getQwenImageModel(
            AiModelEntity modelEntity, ChatSetting chatSetting, ConversationSetting conversationSetting, ImageSetting imageSetting) {
        return null;
    }
}
