package org.astrsomn.deepseek;

import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;

public class DeepSeekExtensionDescriptor extends AstroExtensionDescriptor {

    @Override
    public String getExtensionKey() {
        return "deepseek";
    }

    @Override
    public SystemExtensionEnum.ExtensionTypeEnum getExtensionType() {
        return SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER;
    }

    @Override
    public String getName() {
        return "DeepSeek Model Provider";
    }

    @Override
    public String getDescription() {
        return "DeepSeek provider (OpenAI-compatible API); 模型清单与调用细节可在本模块内扩展。";
    }
}
