package org.astrsomn.server.service;

import org.astrsomn.core.common.base.BaseResponse;

public interface SystemExtensionModelSyncService {

    /**
     * 从 SPI {@link org.astrsomn.core.common.langchain.extension.ModelProviderHandler#getAvailableModels()} 导入模型到当前环境。
     */
    BaseResponse<String> loadModels(Long extensionId);

    /**
     * 按厂商删除当前环境下该 provider 的模型；被 AI 实例引用的模型键会跳过。
     */
    BaseResponse<String> unloadModels(Long extensionId);
}
