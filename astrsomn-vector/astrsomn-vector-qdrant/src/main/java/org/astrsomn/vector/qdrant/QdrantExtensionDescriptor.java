package org.astrsomn.vector.qdrant;

import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;

public class QdrantExtensionDescriptor extends AstroExtensionDescriptor {

    @Override
    public String getExtensionKey() {
        return QdrantVecStoreBackend.EXTENSION_KEY;
    }

    @Override
    public SystemExtensionEnum.ExtensionTypeEnum getExtensionType() {
        return SystemExtensionEnum.ExtensionTypeEnum.VECTOR_STORE;
    }

    @Override
    public String getName() {
        return "Qdrant Vector Store";
    }

    @Override
    public String getDescription() {
        return "Qdrant 向量库；基于 LangChain4j Qdrant 集成与 HTTP 管理 API。";
    }

    @Override
    public String getAvatar() {
        return "";
    }
}
