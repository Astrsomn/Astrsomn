package org.astrsomn.zhipu;

import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;

public class ZhipuExtensionDescriptor extends AstroExtensionDescriptor {

    @Override
    public String getExtensionKey() {
        return "zhipu";
    }

    @Override
    public SystemExtensionEnum.ExtensionTypeEnum getExtensionType() {
        return SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER;
    }

    @Override
    public String getName() {
        return "Zhipu GLM Model Provider";
    }

    @Override
    public String getAuthor() {
        return "Zhipu AI";
    }

    @Override
    public String getDescription() {
        return "智谱 GLM 对话与向量模型；参数与模型列表可按业务扩展。";
    }
}
