package org.astrsomn.qianfan;

import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;

public class QianFanExtensionDescriptor extends AstroExtensionDescriptor {

    @Override
    public String getExtensionKey() {
        return "qianfan";
    }

    @Override
    public SystemExtensionEnum.ExtensionTypeEnum getExtensionType() {
        return SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER;
    }

    @Override
    public String getName() {
        return "Baidu Qianfan Model Provider";
    }

    @Override
    public String getDescription() {
        return "千帆大模型平台接入；需配置 apiKey + secretKey（对应 ModelSetting.apiKey / apiSecret）。";
    }
}
