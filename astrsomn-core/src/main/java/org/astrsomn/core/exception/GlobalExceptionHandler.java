package org.astrsomn.core.exception;


import org.astrsomn.core.common.base.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     * @param e 业务异常
     * @return 响应对象
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> handleBusinessException(BusinessException e) {
        logger.error("Business exception: {}", e.getMessage(), e);
        return BaseResponse.fail(e.getMessage());
    }

    /**
     * 处理系统异常
     * @param e 系统异常
     * @return 响应对象
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> handleRuntimeException(RuntimeException e) {
        logger.error("Runtime exception: {}", e.getMessage(), e);
        return BaseResponse.fail("系统内部错误");
    }

    /**
     * 处理其他异常
     * @param e 异常
     * @return 响应对象
     */
    @ExceptionHandler(Exception.class)
    public BaseResponse<?> handleException(Exception e) {
        logger.error("Exception: {}", e.getMessage(), e);
        return BaseResponse.fail("系统内部错误");
    }
}
