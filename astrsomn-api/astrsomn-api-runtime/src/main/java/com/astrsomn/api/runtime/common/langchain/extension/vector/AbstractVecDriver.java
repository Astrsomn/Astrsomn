package com.astrsomn.api.runtime.common.langchain.extension.vector;

import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;

/**
 * 向量插件基类：具体厂商模块继承本类并实现 {@link VecDriver} 契约。
 */
public abstract class AbstractVecDriver implements VecDriver {

    @Override
    public abstract String getExtensionKey();

    @Override
    public abstract VecSource bindSource(AiVecSourceEntity source);
}
