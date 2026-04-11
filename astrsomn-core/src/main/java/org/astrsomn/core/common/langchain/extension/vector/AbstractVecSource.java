package org.astrsomn.core.common.langchain.extension.vector;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;


@Getter
@AllArgsConstructor
public abstract class AbstractVecSource implements VecSource {

    private final AiVecSourceEntity entity;

    @Override
    public abstract boolean testConnection();

    @Override
    public abstract void shutdown();

    @Override
    public abstract AbstractVecStore openStore(AiVecStoreEntity store);
}
