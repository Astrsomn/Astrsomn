package org.astrsomn.vector.milvus.service;

import org.astrsomn.core.common.constant.AiVecDriverEnum;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecDriver;
import org.astrsomn.core.common.langchain.extension.vector.VecSource;

public final class MilvusDriverHandler extends AbstractVecDriver {

    @Override
    public String getExtensionKey() {
        return AiVecDriverEnum.Provider.MILVUS.getCode();
    }

    @Override
    public VecSource bindSource(AiVecSourceEntity source) {
        return new MilvusVecSourceHandler(source);
    }
}
