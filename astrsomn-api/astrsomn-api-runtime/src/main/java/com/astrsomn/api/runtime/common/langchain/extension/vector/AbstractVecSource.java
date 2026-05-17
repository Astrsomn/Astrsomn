package com.astrsomn.api.runtime.common.langchain.extension.vector;

import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;


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