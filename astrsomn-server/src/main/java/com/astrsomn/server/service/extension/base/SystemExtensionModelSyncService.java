package com.astrsomn.server.service.extension.base;

import com.astrsomn.system.dto.extension.ExtensionModelLoadPreviewDTO;
import com.astrsomn.system.dto.extension.ExtensionModelUnloadPreviewDTO;
import com.astrsomn.api.runtime.common.langchain.extension.model.ModelProviderHandler;
import com.astrsomn.common.base.BaseResponse;

public interface SystemExtensionModelSyncService {

    
    BaseResponse<String> loadModels(Long extensionId, String modelKeys);

    
    BaseResponse<String> unloadModels(Long extensionId, String modelKeys);

    
    BaseResponse<ExtensionModelLoadPreviewDTO> previewLoadModels(Long extensionId);

    
    BaseResponse<ExtensionModelUnloadPreviewDTO> previewUnloadModels(Long extensionId);
}
