package com.astrsomn.api.runtime.common.langchain.extension.vector;

import com.astrsomn.api.vector.entity.AiVecSourceEntity;


public abstract class AbstractVecDriver implements VecDriver {

    @Override
    public abstract String getExtensionKey();

    @Override
    public abstract VecSource bindSource(AiVecSourceEntity source);
}