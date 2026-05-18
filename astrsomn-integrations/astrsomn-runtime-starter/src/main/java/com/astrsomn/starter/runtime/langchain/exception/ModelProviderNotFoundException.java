package com.astrsomn.starter.runtime.langchain.exception;


public class ModelProviderNotFoundException extends AstroModelException {

    public ModelProviderNotFoundException(String extensionCode) {
        super(ErrorCode.MODEL_PROVIDER_NOT_FOUND, "extensionCode=" + extensionCode,
                context().put("extensionCode", extensionCode).build());
    }
}
