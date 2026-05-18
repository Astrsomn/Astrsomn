package com.astrsomn.starter.runtime.langchain.exception;


public class ModelNotFoundException extends AstroConfigException {

    public ModelNotFoundException(String modelKey, String envCode) {
        super(ErrorCode.MODEL_NOT_FOUND, "modelKey=" + modelKey + ", envCode=" + envCode,
                context().put("modelKey", modelKey).put("envCode", envCode).build());
    }
}
