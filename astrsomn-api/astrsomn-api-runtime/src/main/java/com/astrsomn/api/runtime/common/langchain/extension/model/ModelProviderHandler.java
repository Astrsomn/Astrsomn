package com.astrsomn.api.runtime.common.langchain.extension.model;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;

import java.util.List;

public interface ModelProviderHandler {


    AiModelEnum.ProviderEnum getProvider();


    <T> T createModel(Class<T> modelClass, AstroChatParam<?> param);


    List<AiModelEntity> getAvailableModels();


    default String getVersion() {
        return "1.0.0";
    }


    default String getAuthor() {
        return "Astrsomn";
    }
}