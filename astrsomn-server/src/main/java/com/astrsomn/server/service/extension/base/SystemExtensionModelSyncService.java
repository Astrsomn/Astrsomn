package com.astrsomn.server.service.extension.base;

import com.astrsomn.core.common.base.BaseResponse;
import com.astrsomn.core.common.dto.extension.ExtensionModelLoadPreviewDTO;
import com.astrsomn.core.common.dto.extension.ExtensionModelUnloadPreviewDTO;
import com.astrsomn.core.common.langchain.extension.model.ModelProviderHandler;

public interface SystemExtensionModelSyncService {

    /**
     * 从 SPI {@link ModelProviderHandler#getAvailableModels()} 导入模型到当前环境。
     */
    BaseResponse<String> loadModels(Long extensionId, String modelKeys);

    /**
     * 按厂商删除当前环境下该 provider 的模型；被 AI 实例引用的模型键会跳过。
     */
    BaseResponse<String> unloadModels(Long extensionId, String modelKeys);

    /**
     * 预览加载模型：将新增 vs 已存在跳过（不写库）。
     */
    BaseResponse<ExtensionModelLoadPreviewDTO> previewLoadModels(Long extensionId);

    /**
     * 预览卸载模型：将删除 vs 因实例引用保留（不删库）。
     */
    BaseResponse<ExtensionModelUnloadPreviewDTO> previewUnloadModels(Long extensionId);
}
