package com.astrsomn.api.runtime.common.langchain.extension.vector;

import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;


public interface VecSource {

    AiVecSourceEntity getEntity();


    boolean testConnection();


    void shutdown();


    VecStore openStore(AiVecStoreEntity store);
}