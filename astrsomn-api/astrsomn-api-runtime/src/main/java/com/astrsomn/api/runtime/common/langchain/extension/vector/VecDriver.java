package com.astrsomn.api.runtime.common.langchain.extension.vector;

import com.astrsomn.api.vector.entity.AiVecDriverEntity;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;


public interface VecDriver {


    String getExtensionKey();


    VecSource bindSource(AiVecSourceEntity source);

    default String getVersion() {
        return "1.0.0";
    }

    default String getAuthor() {
        return "Astrsomn";
    }

    AiVecDriverEntity getDriverEntity();
}