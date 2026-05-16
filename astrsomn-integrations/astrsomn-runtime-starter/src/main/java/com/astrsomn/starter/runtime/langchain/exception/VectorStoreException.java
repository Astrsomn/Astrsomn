package com.astrsomn.starter.runtime.langchain.exception;

/**
 * RAG 向量存储异常。
 */
public class VectorStoreException extends AstroToolException {

    public VectorStoreException(String detail) {
        super(ErrorCode.VECTOR_STORE_NOT_FOUND, detail);
    }
}
