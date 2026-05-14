package com.astrsomn.api.runtime.common.langchain.extension.vector;

import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;


public abstract class AbstractVecDriver implements VecDriver {

    @Override
    public abstract String getExtensionKey();

    @Override
    public abstract VecSource bindSource(AiVecSourceEntity source);
}