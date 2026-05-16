package com.astrsomn.starter.runtime.langchain.exception;

/**
 * SPI 找不到指定厂商的 ModelProviderHandler。
 */
public class ModelProviderNotFoundException extends AstroModelException {

    public ModelProviderNotFoundException(String extensionCode) {
        super(ErrorCode.MODEL_PROVIDER_NOT_FOUND, "extensionCode=" + extensionCode,
                context().put("extensionCode", extensionCode).build());
    }
}
