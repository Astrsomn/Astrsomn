package com.astrsomn.api.runtime.common.langchain.extension.vector;

import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;
import com.astrsomn.api.runtime.common.entity.AiVecStoreEntity;


public interface VecSource {

    AiVecSourceEntity getEntity();

    
    boolean testConnection();

    
    void shutdown();

    
    VecStore openStore(AiVecStoreEntity store);
}