package org.astrsomn.core.common.langchain.extension.vector;

import org.astrsomn.core.common.entity.AiVecSourceEntity;

/**
 * 向量插件基类：具体厂商模块继承本类并实现 {@link VecDriver} 契约。
 */
public abstract class AbstractVecDriver implements VecDriver {

    @Override
    public abstract String getExtensionKey();

    @Override
    public abstract VecSource bindSource(AiVecSourceEntity source);
}
