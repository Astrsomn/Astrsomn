package org.astrsomn.vector.chroma.config;

import org.astrsomn.core.common.constant.AiVecDriverEnum;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;

public class ChromaExtensionDescriptor extends AstroExtensionDescriptor {

    @Override
    public String getExtensionKey() {
        return AiVecDriverEnum.Provider.CHROMA.getCode();
    }

    @Override
    public SystemExtensionEnum.ExtensionTypeEnum getExtensionType() {
        return SystemExtensionEnum.ExtensionTypeEnum.VECTOR_STORE;
    }

    @Override
    public String getAvatar() {
        return "";
    }

    @Override
    public String getName() {
        return AiVecDriverEnum.Provider.CHROMA.getDesc();
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getAuthor() {
        return "Astrsomn";
    }

    @Override
    public String getDescription() {
        return "Chroma vector database integration (LangChain4j).";
    }
}