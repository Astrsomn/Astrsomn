package org.astrsomn.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.base.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<?> handleBusinessException(BusinessException e) {
        // TODO: 处理业务异常
        log.error("业务异常: {}", e.getMessage(), e);
        return BaseResponse.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleValidationException(MethodArgumentNotValidException e) {
        // TODO: 处理参数校验异常
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.error("参数校验异常: {}", message);
        return BaseResponse.fail(400, message);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleBindException(BindException e) {
        // TODO: 处理绑定异常
        FieldError fieldError = e.getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        log.error("参数绑定异常: {}", message);
        return BaseResponse.fail(400, message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleIllegalArgumentException(IllegalArgumentException e) {
        // TODO: 处理非法参数异常
        log.error("非法参数异常: {}", e.getMessage());
        return BaseResponse.fail(400, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<?> handleException(Exception e) {
        // TODO: 处理系统异常
        log.error("系统异常: {}", e.getMessage(), e);
        return BaseResponse.fail(500, "系统内部错误");
    }
}
