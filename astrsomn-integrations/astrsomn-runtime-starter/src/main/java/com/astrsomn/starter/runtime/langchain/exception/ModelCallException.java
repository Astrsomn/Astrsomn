package com.astrsomn.starter.runtime.langchain.exception;

/**
 * 模型 API 调用失败（包装原始异常）。
 */
public class ModelCallException extends AstroModelException {

    public ModelCallException(String detail, Throwable cause) {
        super(ErrorCode.MODEL_CALL_FAILED, detail, cause);
    }

    public ModelCallException(String detail, Throwable cause, String modelKey) {
        super(ErrorCode.MODEL_CALL_FAILED, detail, context().put("modelKey", modelKey).build(), cause);
    }
}
