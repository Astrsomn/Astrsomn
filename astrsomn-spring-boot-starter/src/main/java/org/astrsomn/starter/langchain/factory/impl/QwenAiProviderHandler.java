package org.astrsomn.starter.langchain.factory.impl;

import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiAccountEntity;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.starter.langchain.factory.core.ModelProviderHandler;

public class QwenAiProviderHandler implements ModelProviderHandler {
    @Override
    public AiModelEnum.ProviderEnum getProvider() {
        return null;
    }

    @Override
    public <T> T createModel(Class<T> modelClass, AiModelEntity modelEntity, AiAccountEntity accountEntity, AstroChatParam<?> param) {
        return null;
    }
}
