package org.astrsomn.starter.langchain.factory.core;

import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiAccountEntity;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;

public interface ModelProviderHandler {

    /**
     * 是否支持该厂商
     * @return
     */
    AiModelEnum.ProviderEnum getProvider();


    /**
     * 创建具体的模型实例
     * @param modelClass
     * @param modelEntity
     * @param accountEntity
     * @param param
     * @return
     * @param <T>
     */
    <T> T createModel(Class<T> modelClass,
                      AiModelEntity modelEntity,
                      AiAccountEntity accountEntity,
                      AstroChatParam<?> param);
}
