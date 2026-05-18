package com.astrsomn.starter.runtime.langchain.exception;


public class VectorStoreException extends AstroToolException {

    public VectorStoreException(String detail) {
        super(ErrorCode.VECTOR_STORE_NOT_FOUND, detail);
    }
}
