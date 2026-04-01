package org.astrsomn.qwen;

import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;

import java.util.List;

public class QwenExtensionDescriptor extends AstroExtensionDescriptor {

    @Override
    public String getExtensionKey() {
        return "";
    }

    @Override
    public SystemExtensionEnum.ExtensionTypeEnum getExtensionType() {
        return null;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getVersion() {
        return "";
    }

    @Override
    public List<AiModelEntity> getSupportedModels() {
        return List.of();
    }
}
