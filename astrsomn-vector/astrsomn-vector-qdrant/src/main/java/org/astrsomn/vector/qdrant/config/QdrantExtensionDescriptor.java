package org.astrsomn.vector.qdrant.config;

import org.astrsomn.core.common.constant.AiVecDriverEnum;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;

public class QdrantExtensionDescriptor extends AstroExtensionDescriptor {


    private static final String AVATAR_SVG = "";


    @Override
    public String getExtensionKey() {
        return AiVecDriverEnum.Provider.QDRANT.getCode();
    }

    @Override
    public SystemExtensionEnum.ExtensionTypeEnum getExtensionType() {
        return null;
    }

    @Override
    public String getAvatar() {
        return "";
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
    public String getAuthor() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }
}
