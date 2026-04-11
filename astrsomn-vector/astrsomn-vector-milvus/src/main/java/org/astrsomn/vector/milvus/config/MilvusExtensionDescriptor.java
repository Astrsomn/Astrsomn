package org.astrsomn.vector.milvus.config;

import org.astrsomn.core.common.constant.AiVecDriverEnum;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;

public class MilvusExtensionDescriptor extends AstroExtensionDescriptor {

    @Override
    public String getExtensionKey() {
        return AiVecDriverEnum.Provider.MILVUS.getCode();
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
        return AiVecDriverEnum.Provider.MILVUS.getDesc();
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
        return "Milvus / Zilliz vector database integration (LangChain4j).";
    }
}
