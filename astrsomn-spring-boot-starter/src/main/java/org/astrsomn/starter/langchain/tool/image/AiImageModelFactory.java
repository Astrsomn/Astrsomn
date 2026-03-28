package org.astrsomn.starter.langchain.tool.image;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import dev.langchain4j.model.image.ImageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.ChatSetting;
import org.astrsomn.core.common.langchain.buildParam.setting.ModelSetting;
import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiImageModelFactory {

    private final AiModelMapper aiModelMapper;
    private final AstrsomnProperties astrsomnProperties;

    public <T> ImageModel getImageModel(AstroChatParam<T> param) {

        ModelSetting modelSetting = param.getModelSetting();
        ChatSetting chatSetting = param.getChatSetting();
        AiModelEntity modelEntity = aiModelMapper.selectOne(new LambdaUpdateWrapper<AiModelEntity>()
                .eq(AiModelEntity::getModelKey, param.getModelKey())
                .eq(AiModelEntity::getEnvCode, astrsomnProperties.getEnvCode()));
        AiModelEnum.ProviderEnum providerEnum =
                AiModelEnum.ProviderEnum.fromCode(modelEntity.getProvider());

        if (Objects.isNull(providerEnum)) {
            log.error("====>  Astrsomn  ====>  未找到匹配的模型提供商");
            throw new RuntimeException("");
        }

        switch (providerEnum) {
            case ALIBABA:
                return getQwenImageModel(modelEntity, modelSetting, chatSetting);
            case OPENAI:
                return getImageModel(modelEntity, modelSetting, chatSetting);
            case DEEPSEEK:
                return getImageModel(modelEntity, modelSetting, chatSetting);
            case ZHIPU:
                return getZhiPuImageModel(modelEntity, modelSetting, chatSetting);
            case QIANFAN:
                return getQianfanImageModel(modelEntity, modelSetting, chatSetting);
            case GOOGLE:
                return getGoogleGeminiImageModel(modelEntity, modelSetting, chatSetting);
            // 可以继续加其他 case
            default:
                log.error("====>  Astrsomn  ====> 未处理的 provider 类型: {} <====", providerEnum);
                throw new RuntimeException("未知的大模型参数");
        }
    }

    private ImageModel getGoogleGeminiImageModel(AiModelEntity modelEntity, ModelSetting modelSetting, ChatSetting chatSetting) {
        return null;
    }

    private ImageModel getQianfanImageModel(AiModelEntity modelEntity, ModelSetting modelSetting, ChatSetting chatSetting) {
        return null;
    }

    private ImageModel getZhiPuImageModel(AiModelEntity modelEntity, ModelSetting modelSetting, ChatSetting chatSetting) {
        return null;
    }

    private ImageModel getImageModel(AiModelEntity modelEntity, ModelSetting modelSetting, ChatSetting chatSetting) {
        return null;
    }

    private ImageModel getQwenImageModel(AiModelEntity modelEntity, ModelSetting modelSetting, ChatSetting chatSetting) {
        return null;
    }


}
