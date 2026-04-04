package org.astrsomn.openai;

import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;

public class OpenAiExtensionDescriptor extends AstroExtensionDescriptor {

    private static final String AVATAR_SVG =
            loadClasspathUtf8(OpenAiExtensionDescriptor.class, "/avatar/openai.svg");

    @Override
    public String getExtensionKey() {
        return "openai";
    }

    @Override
    public SystemExtensionEnum.ExtensionTypeEnum getExtensionType() {
        return SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER;
    }

    @Override
    public String getAvatar() {
        return AVATAR_SVG;
    }

    @Override
    public String getName() {
        return "OpenAI Model Provider";
    }

    @Override
    public String getDescription() {
        return "OpenAI 及 OpenAI 兼容 API；可在本模块扩展 Image 等能力。";
    }
}
