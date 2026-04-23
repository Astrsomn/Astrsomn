package com.astrsomn.core.common.langchain.extension.model;

import com.astrsomn.core.common.constant.AiModelEnum;
import com.astrsomn.core.common.entity.AiModelEntity;
import com.astrsomn.core.common.langchain.buildParam.AstroChatParam;

import java.util.List;

public interface ModelProviderHandler {

    /**
     * 是否支持该厂商
     * @return
     */
    AiModelEnum.ProviderEnum getProvider();


    /**
     * 创建具体的模型实例
     * @param modelClass
     * @param param
     * @return
     * @param <T>
     */
    <T> T createModel(Class<T> modelClass, AstroChatParam<?> param);


    /**
     * 获取可用模型
     * @return
     */
    List<AiModelEntity> getAvailableModels();

    /**
     * 默认版本
     * @return
     */
    default String getVersion() { return "1.0.0"; }

    /**
     * 默认作者
     * @return
     */
    default String getAuthor() { return "Astrsomn"; }
}
