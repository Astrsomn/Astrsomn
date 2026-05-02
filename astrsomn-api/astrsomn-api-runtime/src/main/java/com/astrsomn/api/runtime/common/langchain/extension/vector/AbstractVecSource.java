package com.astrsomn.api.runtime.common.langchain.extension.vector;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;
import com.astrsomn.api.runtime.common.entity.AiVecStoreEntity;


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
