package org.astrsomn.qwen;

import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;

/**
 * 扩展元数据：通过 Java SPI(ServiceLoader) 被系统注册。
 */
public class QwenExtensionDescriptor extends AstroExtensionDescriptor {

    @Override
    public String getExtensionKey() {
        return "qwen";
    }

    @Override
    public SystemExtensionEnum.ExtensionTypeEnum getExtensionType() {
        return SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER;
    }

    @Override
    public String getName() {
        return "Qwen Model Provider";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getAuthor() {
        return "Alibaba Cloud";
    }

    @Override
    public String getDescription() {
        return "Qwen provider extension for chat and embedding models.";
    }
}
