package com.astrsomn.server.service.ai.extension;

import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.system.dto.extension.ExtensionModelLoadPreviewDTO;
import com.astrsomn.system.dto.extension.ExtensionModelUnloadPreviewDTO;

public interface SystemExtensionModelSyncService {


    BaseResponse<String> loadModels(Long extensionId, String modelKeys, Long accountId);


    BaseResponse<String> unloadModels(Long extensionId, String modelKeys);


    BaseResponse<ExtensionModelLoadPreviewDTO> previewLoadModels(Long extensionId, Long accountId);


    BaseResponse<ExtensionModelUnloadPreviewDTO> previewUnloadModels(Long extensionId);
}
